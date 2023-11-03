public interface QueueType<T> {
    public void enqueue(T item) throws Exception;

    public T dequeue() throws Exception;

    public T element() throws Exception;

    public boolean isEmpty();
}

