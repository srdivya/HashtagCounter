package com.HashtagCounter;

/**
 * 
 */

/**
 * @author Divya
 *
 */
import java.io.*;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class HashtagCounter 
{
	public static void main(String[] args) 
	{
		try 
		{
			//Gets the path as an argument during runtime
			Path path = Paths.get(args[0]);
			//Hashtable that contains the hashtags as keys while the pointers to FibonacciNodes are the values.
		    Hashtable<String, FibonacciHeapNode> hashTable = new Hashtable<>();
		    Charset charset = Charset.forName("UTF-8");
		    List<String> lstInput = Files.readAllLines(path, charset);
		    File fout = new File("output_file.txt");	
			FileWriter fw = new FileWriter(fout, true);
		 
			BufferedWriter bw = new BufferedWriter(fw);
		    FibonacciHeapNode node; 
		    FibonacciHeap fibHeap = new FibonacciHeap();
		    List<String> lstOutput = new ArrayList<String>();
		    for (String strTemp : lstInput)
		    {
		    	//First case where the hashtags are to be added to the Fibonacci heap
		    	if(strTemp.startsWith("#"))
		    	{
		    		String [] strArr = strTemp.split(" ");
		    		String strHashtag = strArr[0].substring(1, strArr[0].length());
		    		FibonacciHeapNode tempNode = (FibonacciHeapNode) hashTable.get(strHashtag);
		    		if (tempNode == null)
		    		{
		    			node = new FibonacciHeapNode(Integer.parseInt(strArr[1]));
			    		hashTable.put(strHashtag, node);
			    		fibHeap.Insert(node);
		    		}
		    		//If a node exists with the same hashtag, increment it with the new value by calling increaseKey
		    		else
		    		{
		    			fibHeap.increaseKey(tempNode, Integer.parseInt(strArr[1]));
		    		}
		    	}
		    	//If the input is a number then print the 'n' top Hashtags by calling removeMax. Then reinsert them back into the heap.
		    	else if(isNumeric(strTemp))
		    	{
		    		System.out.println(hashTable);
		    		int nQuery = Integer.parseInt(strTemp);
		    		String strOutput = "";
		    		List<FibonacciHeapNode> tempNode = new ArrayList<FibonacciHeapNode>();
		    		for(int i = 0; i < nQuery; i++)
		    		{
		    			tempNode.add(fibHeap.removeMax());
		    			for(Map.Entry entry: hashTable.entrySet())
						{
							if(tempNode.get(i).equals(entry.getValue()))
			                {
			                     strOutput = strOutput + entry.getKey() + ", ";
			                     break;
			                }
			            }
    				}
		    		lstOutput.add(strOutput.substring(0, strOutput.length()-2));
		    		for(int i = 0; i < nQuery; i++)
		    		{
		    			fibHeap.Insert(tempNode.get(i));
		    		}
		    	}
		    	//Exit the loop when you encounter a 'stop'
		    	else if(strTemp.toLowerCase().equals("stop"))
		    		break;
		    }
		    //Prints the list of hashtags that are stored in lstOutput
		    for (String str : lstOutput) 
		    {
			    bw.write(str);
			    bw.newLine();
			}
		    bw.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			
		}
	}
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
