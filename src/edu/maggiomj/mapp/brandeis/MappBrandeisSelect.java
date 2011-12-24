package edu.maggiomj.mapp.brandeis;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MappBrandeisSelect extends Activity {
		
	Activity activity = this;
	
	final int SPINNER_ID = android.R.layout.simple_spinner_dropdown_item;
	final static int MaxVertices = 175;
	final static int MaxEdges = 600;
	
	String[] vertexNames = new String[MaxVertices];
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Spinner toSpinner = (Spinner) findViewById(R.id.beginSpinner);
        final Spinner fromSpinner = (Spinner) findViewById(R.id.finishSpinner);
        
        final CheckBox wheelsCheck = (CheckBox) findViewById(R.id.wheelsCheck);
        
        final RadioGroup minGroup = (RadioGroup) findViewById(R.id.minimizeRadio);
        
        Button button = (Button) findViewById(R.id.goButton);
        /*
        Vertex[] vertices = null;
        try {
			vertices = readVertexFile();
		} catch (FileNotFoundException e) {
			Log.e("Select", "file not found");
			finish();
		}
		
		int i = 0;
		
		for(Vertex vertex : vertices) {
			if(vertex != null) {
				if(vertex.index > 4) {
					vertexNames[i] = vertex.name;
					i++;
				}
			}
		}
		
		ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(this, SPINNER_ID, vertexNames);
		ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this, SPINNER_ID, vertexNames);
		*/
        
        ArrayAdapter<CharSequence> fromAdapter = ArrayAdapter.createFromResource(this, R.array.vertices, SPINNER_ID);
		ArrayAdapter<CharSequence> toAdapter = ArrayAdapter.createFromResource(this, R.array.vertices, SPINNER_ID);
        
		fromSpinner.setAdapter(fromAdapter);
		toSpinner.setAdapter(toAdapter);
		
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				int begin = toSpinner.getSelectedItemPosition()+5;
				int finish = fromSpinner.getSelectedItemPosition()+5;
				boolean wheels = wheelsCheck.isChecked();
				int timeId = minGroup.getCheckedRadioButtonId();
				boolean time;
				if(timeId == R.id.distanceRadio) 
					time = false;
				else
					time = true;
				
				
				Intent intent = new Intent(activity, MappBrandeisGo.class);
				intent.putExtra("Begin", begin);
				intent.putExtra("Finish", finish);
				intent.putExtra("Wheels", wheels);
				intent.putExtra("Time", time);
				
				startActivity(intent);
				
			}});
        
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
		
		Vertex [] vertices = new Vertex[MaxVertices];
		
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
}