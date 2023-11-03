/***
 *  Linked list style queue
 * @param <T>
 */
public class LinkedQueue<T> implements QueueType<T> {
    private LLNode<T> head, tail;

    LinkedQueue(){
        head = tail = null;
    }
    public void enqueue(T item){
        LLNode<T> newNode = new LLNode<>(item);

        if(isEmpty()){
            head = tail = newNode;
            return;
        }
        tail.next = newNode;
        tail = newNode;
    }

    public T dequeue() throws Exception{
        if(isEmpty()){
            throw new Exception("Queue underflow.");
        }
        if(head == tail){
            tail = tail.next;
        }
        LLNode<T> curr = head;
        head = head.next;

        return curr.data;
    }

    public T element() throws Exception{
        if(isEmpty()){
            throw new Exception("Queue underflow.");
        }
        return head.data;
    }

    public boolean isEmpty(){
        return head == null;
    }
    private class LLNode<T>{
        T data;
        LLNode<T> next;

        LLNode(T info){
            data = info;
            next = null;
        }
    }
}

