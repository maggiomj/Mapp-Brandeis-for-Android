package edu.maggiomj.mapp.brandeis;

import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

public class MappAdjacencyList {	
	
	public class AdjNode {
		
		int vertex;
		int cost;
		int edgeIndex;
		String code;
		
		public AdjNode(int vert, int c, int index, String co) {
			vertex = vert;
			cost = c;
			edgeIndex = index;
			code = co;
		}
		
	}
	
	private ArrayList<AdjNode>[] adjList;
	
	public MappAdjacencyList (Edge[] edges) {
		adjList =  new ArrayList[MappBrandeisSelect.MaxEdges];
		
		for(int i = 0; i < MappBrandeisSelect.MaxEdges; i++) {
			adjList[i] = new ArrayList<AdjNode>();
		}
		
		for(Edge edge : edges) {
			
			if(edge != null) {
				AdjNode node = new AdjNode(edge.end, edge.length, edge.index, edge.code);
				adjList[edge.start].add(node);
			}
		}
	}
	
	/*public void initialize(Edge[] edges) {
		
		for(Edge edge : edges) {
			
			if(edge != null) {
				AdjNode node = new AdjNode(edge.end, edge.length, edge.index, edge.code);
				adjList[edge.start].add(node);
			}
		}
		
	}*/
	
	public ArrayList<AdjNode> getList(int v) {
		return adjList[v];
	}
	

}
