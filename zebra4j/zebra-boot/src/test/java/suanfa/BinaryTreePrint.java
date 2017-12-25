package suanfa;

import java.util.Arrays;

/**
 * Created by lzy@js-dev.cn on 2017/1/14 0014.
 */
public class BinaryTreePrint {

	public static Node buildTree(int[] pre, int[] middle) {

		if (pre != null && middle != null) {
			if( pre.length != middle.length) {
				System.out.println("参数非法");
			}

			Node root = new Node();
			for (int i = 0; i < middle.length; i++) {
				if (middle[i] == pre[0]) {
					root.data = middle[i];
					root.left = buildTree( Arrays.copyOfRange(pre, i, i+1), Arrays.copyOfRange(middle, 0, i));
					root.right = buildTree( Arrays.copyOfRange(pre, i+1, pre.length), Arrays.copyOfRange( middle, i+1, middle.length) );
				}
			}

			return root;

		}
		return null;
	}


	public static void main(String[] args) {
		int[] pre = {1, 2, 4, 7, 3, 5, 6, 8};
		int[] middle = {4, 7, 2, 1, 5, 3, 8, 6};
		Node<Integer> root = buildTree(pre, middle);
	}


}

class Node<T> {
	T data;
	Node left;
	Node right;
}