package suanfa;


import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by lzy@js-dev.cn on 2017/1/13 0013.
 */
public class PrintList {

	public static void printResers(ListNode head) {
		Stack<ListNode> stack = new Stack<>();
		while (head != null) {
			stack.push(head);
			head = head.next;
		}
		while (!stack.isEmpty()) {
			System.out.println(stack.pop().data);
		}
	}

	public static void print(ListNode head) {
		List<ListNode> list = new ArrayList<ListNode>();
		while (head != null) {
			list.add(head);
			head = head.next;
		}
		if (CollectionUtils.isNotEmpty(list)){
			System.out.println(list);
		}
	}

	public static void main(String[] args) {
		ListNode<Integer> node1 = new ListNode<>();
		ListNode<Integer> node2 = new ListNode<>();
		ListNode<Integer> node3 = new ListNode<>();

		node1.data = 1;
		node2.data = 2;
		node3.data = 3;

		node1.next = node2;
		node2.next = node3;

		System.out.println("链表:");
		print(node1);

		System.out.println("翻转链表:");
		printResers(node1);
	}

}

class ListNode<T> {
	T data;
	ListNode next;

	@Override
	public String toString() {
		return String.valueOf(data);
	}
}
