package datatype;

import java.util.Stack;

/**
 * Created by lzy@js-dev.cn on 2017/1/15 0015.
 */
public class StackList<T> {

	private Stack<T> stack1 = new Stack<T>();
	private Stack<T> stack2 = new Stack<T>();

	/**
	 * 添加元素到队尾
	 * @param t
	 */
	public void appendTail(T t) {
		stack1.push(t);
	}

	public void printList(){
		System.out.println("当前栈：" + stack1);
	}

	public T deleteHead() throws Exception{
		if (stack2.isEmpty()) {
			while (!stack1.isEmpty()) {
				stack2.push(stack1.pop());
			}
			if(stack2.isEmpty()){
				System.out.println("队列为空，不可删除");
			}
		}
		//stack2存放了所有的stack的倒序，弹出的第一个就是队列头，也就是栈尾
		return stack2.pop();
	}

	public static void main(String[] args) throws Exception {
		StackList<String> slist = new StackList<>();
		slist.appendTail("1");
		slist.appendTail("2");
		slist.appendTail("3");
		slist.deleteHead();
		System.out.println(slist);
	}


}
