package edu.maggiomj.mapp.brandeis;

public class MappMinHeap {

	
	public final double InfiniteCost = 10000;
	
	
	private class HeapNode {
		
		double cost;
		int vertex;
	
		public HeapNode(int nVertex, double nCost) {
			
			cost = nCost;
			vertex = nVertex;
			
		}
		
	}
	
	HeapNode heap[];
	int size;
	int nextRB;
	
	public MappMinHeap(int nSize) {
		
		size = nSize;
		nextRB = 0;
		heap = new HeapNode[size];
		
	}
	
	public void init(int begin) {
		for(int i = 0; i < size; i++) {
			if(i!=begin)
				insert(i, InfiniteCost);
			else
				insert(i, 0);
		}
	}
	
	
	public int findVertex(int v) {
		
		for (int i = nextRB-1; i>=0; i--) {
			if(heap[i].vertex == v) return i;
		}
		/*not found*/
		return -1;
	}
	
	private int parent(int i) {return i>0? (i-1)/2 : -1;} /*return -1 on error*/
	
	private int leftChild(int i) {return (2*i+1)<=nextRB? 2*i+1 : -1; } /*return -1 on error*/
	
	private int rightChild(int i) {return (2*i+2)<=nextRB? 2*i+2 : -1; } /*return -1 on error*/
	
	private boolean isLeaf(int i) {return leftChild(i) < 0 && rightChild(i) <0;}
	
	public boolean isEmpty() {return nextRB <= 0;}
	
	private boolean isFull() {return nextRB >= size;}
	
	private void swap (int a, int b) {
		HeapNode temp = heap[a];
		heap[a] = heap[b];
		heap[b] = temp;
	}
	
	private void percUp(int i) {
		
		int parentIndex = parent(i);
		
		while(i > 0 && heap[i].cost < heap[parentIndex].cost) {
			swap(i, parentIndex);
			i = parentIndex;
			parentIndex = parent(i);
		}
		
	}
	
	public boolean insert(int vertex, double cost) {
		
		if(isFull()) return false; /*error*/
		
		heap[nextRB] = new HeapNode(vertex, cost);
		percUp(nextRB);
		nextRB++;
		
		return true;
		
	}
	
	private void percDown(int i) {
		
		int lChild = leftChild(i);
		int rChild = rightChild(i);
		
		while(!isLeaf(i) && (heap[i].cost > heap[lChild].cost || heap[i].cost > heap[rChild].cost)) {
			
			int j = lChild;
			if(rChild > 0 && heap[rChild].cost < heap[lChild].cost)
				j = rChild;
			swap(i,j);
			i=j;
			
			lChild = leftChild(i);
			rChild = rightChild(i);
	
		}
	}
	
	public int deleteMin() {
		if(isEmpty()) return -1; // error
		
		--nextRB;
		swap(0, nextRB);
		percDown(0);
		
		return heap[nextRB].vertex;
	}
	
	public boolean changeCost (int v, double nCost) {
		
		int i = findVertex(v);
		
		if (i < 0) return false;
		
		heap[i].cost = nCost;
		percUp(i);
		
		return true;
	}
	
}
