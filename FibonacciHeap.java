 

import java.util.*;

class FibonacciHeapNode
{
    FibonacciHeapNode child, left, right, parent;    
    int key;
    boolean childCut;
    int degree;
    public FibonacciHeapNode(int key)
    {
        this.right = this;
        this.left = this;
        this.key = key;
    }
}
public class FibonacciHeap 
{
	private FibonacciHeapNode maxNode; //Pointer to the maxNode
	private int nNodes; //Count of number of nodes in the heap
	/*Function: insert
	 * Use: Inserts a new node into the Fibonacci heap*/
	public void Insert(FibonacciHeapNode node)
	{
		
		if (maxNode != null)
		{
			node.left = maxNode;
            node.right = maxNode.right;
            maxNode.right = node;
            node.right.left = node;
            //Set the maxNode if the new key is the biggest node
            if (node.key > maxNode.key)
            	maxNode = node;
		}
		//In case of an empty node, make the new node the maxNode
		else
			maxNode = node;
		nNodes++;
	}
	/*Function: increaseKey
	 * Use: It increases the node.key's value by the key value that is passed as parameter to the function.
	 * 		It also calls the cut function if the new key value is greater than the parent's key value*/
	public void increaseKey(FibonacciHeapNode node, int key)
    {   
        node.key += key;

        FibonacciHeapNode tempNode = node.parent;

        if ((tempNode != null) && (node.key > tempNode.key)) 
        {
            cut(node, tempNode);
            cascadingCut(tempNode);
        }
        //Set the current node to maxNode if its bigger than maxNode
        if (node.key > maxNode.key)
            maxNode = node;
    }
	/* Function: removeMax
	  * Use: Removes the maxNode from the heap and it returns the pointer to that node to the calling function
	  * 	 It calls the pairwiseCombine() method after the removeMax has been performed */
	 public FibonacciHeapNode removeMax()
    {
        FibonacciHeapNode node = maxNode;

        if (node != null) 
        {
            int nChildren = node.degree;
            FibonacciHeapNode child = node.child;
            FibonacciHeapNode tempRight;

            //Below block removes all the children of the maxNode and puts it in the root linked list
            while (nChildren > 0) 
        	{
                tempRight = child.right;

                // remove child from child list
                child.left.right = child.right;
                child.right.left = child.left;

                //Add all the children to root list of heap
                child.left = maxNode;
                child.right = maxNode.right;
                maxNode.right = child;
                child.right.left = child;

                //Sets parent of the children to null
                child.parent = null;
                child = tempRight;
                nChildren--;
            }

            // remove maxNode from root list of heap
            node.left.right = node.right;
            node.right.left = node.left;

            if (node == node.right)
            {
            	maxNode = node.child;
            	pairwiseCombine();
            }
        	else 
        	{
                maxNode = node.right;
                pairwiseCombine();
            }
            node.child = null;
            node.parent = null;
            node.degree = 0;
            node.right = node;
            node.left = node;
            // decrement size of heap
            nNodes--;
        }

        return node;
    }
	 /* Function: pairwiseCombine
	  * Use: Performs the pairwise combine operation when a removeMax is performed */
	public void pairwiseCombine()
	{
		int arraySize = nNodes;
		//Array of degrees
        List<FibonacciHeapNode> arrayDegree = new ArrayList<FibonacciHeapNode>(arraySize);

        // Initialize degree array
        for (int i = 0; i < arraySize; i++) 
        	arrayDegree.add(null);
        
        // Find the number of root nodes.
        int nRoots = 0;
        FibonacciHeapNode tempRoot = maxNode;

        if (tempRoot != null) 
        {
        	//For the first right element the count is updated
        	nRoots++;
            tempRoot = tempRoot.right;
            //Below while loop loops through all the root nodes in the heap and counts the number of nodes 
            //till it reaches the maxNode in the circular linked list
            while (tempRoot != maxNode) 
            {
            	if(tempRoot == maxNode)
            		break;
            	nRoots++;
                tempRoot = tempRoot.right;
            }
        }
        //Next step is to identify all the trees in the heap with same degree
        while (nRoots > 0) 
        {
            int nDegree = tempRoot.degree;
            FibonacciHeapNode nextNode = tempRoot.right;

            //Below loop is to identify nodes with the same degree as the tempNode
            while (true) 
            {
            	//Finds the node with the same degree to meld
                FibonacciHeapNode sameDegNode = arrayDegree.get(nDegree);
                if (sameDegNode == null) 
                    break; //Break if there is no other tree with same degree
                
                //Meld the two trees if there are two of the same degree
                //Before that swap the keys if the tempRoot is smaller than the sameDegNode's
                if (tempRoot.key < sameDegNode.key) 
                {
                    FibonacciHeapNode temp = sameDegNode;
                    sameDegNode = tempRoot;
                    tempRoot = temp;
                }
                meld(tempRoot, sameDegNode);

                // Set the number of tree with that degree to null
                arrayDegree.set(nDegree, null);
                nDegree++;
            }

            // Put this melded tree with its degree in the array
            arrayDegree.set(nDegree, tempRoot);

            // Move forward through list.
            tempRoot = nextNode;
            nRoots--;
        }

        //Below block reconstructs the list and finds the new maxNode
        maxNode = null;

        for (int i = 0; i < arraySize; i++) 
        {
            FibonacciHeapNode tempNode = arrayDegree.get(i);
            if (tempNode == null) 
                continue;

            //
            if (maxNode != null) 
            {
                // Below lines removes the root from the linked list
            	tempNode.left.right = tempNode.right;
            	tempNode.right.left = tempNode.left;

                //Below lines adds the tempRoot back to the list
            	tempNode.left = maxNode;
            	tempNode.right = maxNode.right;
                maxNode.right = tempNode;
                tempNode.right.left = tempNode;

                // Checks if the tempNode is the largest key, if so sets it to maxNode
                if (tempNode.key > maxNode.key) 
                	maxNode = tempNode;
            } 
            else 
            	maxNode = tempNode;
        }
	}
	/* Function: meld
	 * Use: To meld two trees*/
	private void meld(FibonacciHeapNode node1, FibonacciHeapNode node2) 
	{
		//Remove sameDegNode from root linked list
		node2.left.right = node2.right;
		node2.right.left = node2.left;
		//Make sameDegNode the child of tempRoot
		node2.parent = node1;
		//Add the sameDegNode to the child linked list
		if (node1.child == null)
		{
			node1.child = node2;
			node2.right = node2;
			node2.left = node2;				
		}
		else
		{
			node2.right = node1.child.right;
			node2.left = node1.child;
			node1.child.right.left = node2;
			node1.child.right = node2;				
		}
		node1.degree++;
		node2.childCut = false; //setting the childCut of node2 to false after it has been melded
	}
	/* Function : cut
	 * Use: To remove a node from a tree and attach it to the Root linked list 
	 * 		Removes node1 from node2 and attaches node1 in the root list*/
	protected void cut(FibonacciHeapNode node1, FibonacciHeapNode node2)
    {
		//Below block removes node1 from node2 and decrement degree of node2
        node1.left.right = node1.right;
        node1.right.left = node1.left;
        node2.degree--;

        //If node1 is the child of node2 then the below block changes it
        if (node2.child == node1) 
            node2.child = node1.right;
        //If node2 had only one child and it was node1, then set the child pointer to null
        if (node2.degree == 0) 
            node2.child = null;

        // add node1 to root list
        node1.right = maxNode.right;
        node1.left = maxNode;
        maxNode.right.left = node1;
        maxNode.right = node1;

        // set parent of node1 to null
        node1.parent = null;

        // set childCut to false when its removed from node2
        node1.childCut = false;
    }
	
	/* Function: cascadingCut
	 * Use: cascadingCut performed when you increaseKey */
	protected void cascadingCut(FibonacciHeapNode node)
    {
        FibonacciHeapNode parent = node.parent;

        // if there's a parent...
        if (parent != null) 
        {
            // if y is unmarked, set it marked
            if (parent.childCut == false)
            	parent.childCut = true;
            else 
            {
                //remove node from the parent
                cut(node, parent);
                //perform cascadingCut for the parent now
                cascadingCut(parent);
            }
        }
    }	
}

