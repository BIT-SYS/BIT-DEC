package test;

import java.util.ArrayList;
import java.util.HashSet;

import core.dissambler.model.AsmInst;


public class PointedRead {

	public static void main(String[] args1) {
		HashSet<Integer> set = new HashSet<>();
		set.add(1);
		set.add(2);
		set.add(1);
		System.out.println(set.toString());
		
		ArrayList<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(33);
		list.add(44);
		list.add(5);
		System.out.println(list.toString());
		list.remove(2);
		System.out.println(list.toString());
	}

}
