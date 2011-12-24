package edu.maggiomj.mapp.brandeis;

public class Edge {

	int index;
	int start;
	int end;
	int length;
	int angle;
	String direction;
	String code;
	String name;
	
	public Edge(int in, int st, int e, int len, int ang, String dir, String co, String nName) {
		
		index = in;
		start = st;
		end = e;
		length = len;
		angle = ang;
		direction = dir;
		code = co;
		name = nName;
	}
	
	
}
