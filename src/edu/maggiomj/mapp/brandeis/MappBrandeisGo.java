package edu.maggiomj.mapp.brandeis;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import edu.maggiomj.mapp.brandeis.MappAdjacencyList.AdjNode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MappBrandeisGo extends Activity{

	/*Speeds (based on 3.1 mph average human walking speed according to Wikipedia).*/
	private final int WalkSpeed  = 272;    /*ft/min = (3.1 miles/hr) * (5280 ft/mile) / (60 mins/hr)*/
	private final double WalkFactorU = 0.9;  /*Multiply walk speed by this for walk up.*/
	private final double WalkFactorD = 1.1;  /*Multiply walk speed by this for walk down.*/
	private final double SkateFactorU = 1.1; /*Multiply walk speed by this for skateboard up.*/
	private final double SkateFactorF = 2.0; /*Multiply walk speed by this for skateboard flat.*/
	private final double SkateFactorD = 5.0; /*Multiply walk speed by this for skateboard down.*/
	private final double StepFactorU = 0.5;  /*Multiply walk speed by this for walk up steps.*/
	private final double StepFactorD = 0.9;  /*Multiply walk speed by this for walk down steps.*/
	private final double BridgeFactor = 1.0; /*Multiply walk speed by this for skateboard down.*/
	
	private final int InfiniteCost = 100000;
	
	private double totalDistance = 0;
	private double totalTime = 0;
	
	private int beginVert;
	private int finishVert;
	
	private boolean wheelsFlag;
	private boolean timeFlag;
	
	private Stack<Integer> pathStack = new Stack<Integer>();
	
	private MappAdjacencyList adjList;
	private MappMinHeap heap = new MappMinHeap(MappBrandeisSelect.MaxVertices);
	
	private ArrayList<String> directions = new ArrayList<String>();
	
	
	private class Point {
		int x;
		int y;
		
		public Point(int nx, int ny) {
			x = nx;
			y = ny;
		}
		
	}
	
	private Vector<Point> points = new Vector<Point>();
	
    Vertex[] vertices = null; 
    Edge[] edges = null;
    
    ProgressDialog pd;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        
        beginVert = this.getIntent().getIntExtra("Begin", -1);
        finishVert = this.getIntent().getIntExtra("Finish", -1);
        
        if(beginVert == -1 || finishVert == -1) {
        	showToast("Internal Error");
        	finish();
        }
        
        wheelsFlag = this.getIntent().getBooleanExtra("Wheels", false);
        timeFlag = this.getIntent().getBooleanExtra("Time", false);
        
        
        pd = ProgressDialog.show(this, "Calculating Route", "Please Wait...");
        final Handler handler = new Handler() {
		   public void handleMessage(Message msg) { pd.dismiss();}
		};
		
		Thread doDijstrka = new Thread() {  
		   public void run() {

			  loadFiles();
			  dijkstra();
			  ListView directionsList = (ListView) findViewById(R.id.directions_list);
			  directionsList.setAdapter(new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, directions ));
		      handler.sendEmptyMessage(0);
	
		   }
		   
		};
		
		doDijstrka.start();
        
    }

	protected void loadFiles() {
		 
		Log.d("go", "begin loading files");
        
        try {
			vertices = readVertexFile();
			edges = readEdgeFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e("Go", "file not found");
			finish();
		}

        Log.d("go", "end loading files");
		
		adjList = new MappAdjacencyList(edges);	
	}

	private void showToast(String string) {
		Toast.makeText(this.getApplicationContext(), string, Toast.LENGTH_SHORT).show();
		
	}

	private void dijkstra() {
		
		double[] cost = new double[MappBrandeisSelect.MaxVertices];
		int[] previous = new int[MappBrandeisSelect.MaxVertices];
		boolean[] marked = new boolean[MappBrandeisSelect.MaxVertices];
		
		for(int j = 0; j<MappBrandeisSelect.MaxVertices; j++) {
			cost[j] = InfiniteCost;
			previous[j] = -1;
			marked[j] = false;
		}
		
		cost[beginVert] = 0;
		
		heap.init(beginVert);
		
		int min;
		
		while(!heap.isEmpty()) {
			
			if((min=heap.deleteMin()) < 0) {
				Log.d("Dijkstra", "heap empty");
				finish();
			}
			
			marked[min] = true;
			
			ArrayList<AdjNode> nodes = adjList.getList(min);
			
			for(AdjNode neighbor : nodes) {
				
				if(!marked[neighbor.vertex]) {
					double edgeCost = neighbor.cost;
					if(timeFlag) {
						double factor = determineSpeed(neighbor.code);
						edgeCost = edgeCost * factor;
					}
					
					if(cost[min] + edgeCost < cost[neighbor.vertex]) {
						cost[neighbor.vertex] = cost[min] + edgeCost;
						previous[neighbor.vertex] = min;
						if(!heap.changeCost(neighbor.vertex, cost[neighbor.vertex])) 
							Log.d("Dijkstra", "edge not found changing cost");
						
					}
				}
				
			}
			if(min == finishVert) break;			
		}
		
		int pathVertex = finishVert;
		
		while(pathVertex != beginVert) {
			pathStack.push(pathVertex);
			Log.d("go", "pathVertex: "+pathVertex);
			pathVertex = previous[pathVertex];
		}
		
		pathStack.push(beginVert);
		
		int popped, topped;
		
		while(true) {
			popped = pathStack.pop();
			points.add(new Point(vertices[popped].x, vertices[popped].y));
			if(pathStack.isEmpty()) break;
			topped = pathStack.peek();
			addEdge(popped, topped);
		}
		
		drawMap();
		
	}
	
	private void drawMap() {
		// TODO Auto-generated method stub
		
	}

	private void addEdge(int s, int t) {

		for(Edge edge : edges) {
			if(edge.start == s && edge.end == t) {
				double factor = determineSpeed(edge.code);
				double seconds = 60*edge.length/factor;
				
				addEdgeToList(edge, seconds);
				
				totalDistance+=edge.length;
				totalTime += seconds;
				
				break;
			}
		}
		
	}

	private void addEdgeToList(Edge edge, double seconds) {
		
		StringBuilder string = new StringBuilder();
		
		String action;
		
		if(edge.code.equals("(f)")) action = "Walk"; 
		else if(edge.code.equals("(F)")) {if(wheelsFlag) action = "Glide"; else action = "Walk";} 
		else if(edge.code.equals("(u)")) action = "Walk up"; 
		else if(edge.code.equals("(U)")) {if(wheelsFlag) action = "Glide up"; else action = "Walk up";} 
		else if(edge.code.equals("(d)")) action = "Walk down";
		else if(edge.code.equals("(D)")) {if(wheelsFlag) action = "Coast down"; else action = "Walk down";} 
		else if(edge.code.equals("(s)")) action = "Climb up"; 
		else if(edge.code.equals("(t)")) action = "Climb down"; 
		else if(edge.code.equals("(b)")) action = "Walk";
		else action = "Walk";
		
		string.append(action+" "+edge.angle+" degrees "+edge.direction+" for "+edge.length+" feet to "+vertices[edge.end].name);
		if(seconds < 60) string.append(" ("+String.format("%.2f", seconds)+ " seconds)");
		else {
			double minutes = (double)seconds/60;
			string.append(" ("+String.format("%.2f", minutes)+ " minutes)");
		}
		
		directions.add(string.toString());
		
	}

	private double determineSpeed(String code) {
		
		
		if(code.equals("(f)")) return WalkSpeed; 
		if(code.equals("(F)")) return wheelsFlag ? SkateFactorF*WalkSpeed : WalkSpeed; 
		if(code.equals("(u)")) return WalkSpeed*WalkFactorU; 
		if(code.equals("(U)")) return wheelsFlag ? WalkSpeed*SkateFactorU : WalkSpeed*WalkFactorU;
		if(code.equals("(d)")) return WalkSpeed*WalkFactorD; 
		if(code.equals("(D)")) return wheelsFlag ? WalkSpeed*SkateFactorD : WalkSpeed*WalkFactorD;
		if(code.equals("(s)")) return WalkSpeed*StepFactorU; 
		if(code.equals("(t)")) return WalkSpeed*StepFactorD; 
		if(code.equals("(b)")) return WalkSpeed*BridgeFactor; 
		//default
		return WalkSpeed;
		
		
		
		
	}
	
	public Vertex[] readVertexFile() throws FileNotFoundException {
        //File vertexFile = new File("res/values/brandeis_vertices.txt");
        InputStream vertexFile = getResources().openRawResource(R.raw.brandeis_vertices);
		Scanner scanner = new Scanner(vertexFile);
		
		int in;
		String label;
		int nx;
		int ny;
		String name;
		
		int i = 0;
		
		Vertex [] vertices = new Vertex[MappBrandeisSelect.MaxVertices];
		
		while(scanner.hasNext()) {
			
			// vertex file order:
			// index label x y name
				
			in = scanner.nextInt();
			label = scanner.next();
			nx = scanner.nextInt();
			ny = scanner.nextInt();
			name = scanner.nextLine();
			
			vertices[i] = new Vertex(in, label, nx, ny, name);
			
			i++;
			
		}
		
		return vertices;
	}
	
	public Edge[] readEdgeFile() throws FileNotFoundException {
        //File edgeFile = new File("res/values/brandeis_edges.txt");
		InputStream edgeFile = getResources().openRawResource(R.raw.brandeis_edges);
		Scanner scanner = new Scanner(edgeFile);
		
		int in;
		int st;
		int end;
		int len;
		int ang;
		String dir;
		String co;
		String name;
		
		int i = 0;
		
		Edge[] edges = new Edge[MappBrandeisSelect.MaxEdges];
		
		while(scanner.hasNext()) {
			
			// edge file order:
			// index start end length angle direction code name
				
			in = scanner.nextInt();
			st = scanner.nextInt();
			end = scanner.nextInt();
			len = scanner.nextInt();
			ang = scanner.nextInt();
			dir = scanner.next();
			co = scanner.next();
			name = scanner.nextLine();
			
			edges[i] = new Edge(in, st, end, len, ang, dir, co, name);

			i++;
			
		}
		
		return edges;
	}
	
}
