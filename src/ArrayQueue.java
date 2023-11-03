public class ArrayQueue<T> implements QueueType<T> {
    T[] arr;
    int head, tail;
    ArrayQueue(){
        arr = (T[]) new Object[10];
        head = tail = 0;
    }
    @Override
    public void enqueue(T item) throws Exception{
        if(isFull()){
            throw new Exception("Queue overflow.");
        }
        arr[tail++] = item;
        tail %= arr.length;
    }

    @Override
    public T dequeue() throws Exception {
        if(isEmpty()){
            throw new Exception("Queue underflow.");
        }
        T temp = arr[head++];
        head %= arr.length;
        return temp;
    }

    @Override
    public T element() throws Exception {
        return null;
    }
    public boolean isFull(){
        return (head + 1) == tail;
    }
    @Override
    public boolean isEmpty() {
        return head == tail;
    }
}