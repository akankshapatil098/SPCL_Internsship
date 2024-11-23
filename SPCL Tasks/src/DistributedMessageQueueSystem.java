import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MessageQueue {
    private final Queue<String> queue = new LinkedList<>();
    private final int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    // Method for producers to add messages to the queue
    public synchronized void produce(String message) throws InterruptedException {
        while (queue.size() == capacity) {
            System.out.println("Queue is full. Producer is waiting...");
            wait(); // Wait until space is available
        }
        queue.add(message);
        System.out.println("Produced: " + message);
        notifyAll(); // Notify consumers
    }

    // Method for consumers to consume messages from the queue
    public synchronized String consume() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("Queue is empty. Consumer is waiting...");
            wait(); // Wait until a message is available
        }
        String message = queue.poll();
        System.out.println("Consumed: " + message);
        notifyAll(); // Notify producers
        return message;
    }
}

class Producer implements Runnable {
    private final MessageQueue queue;

    public Producer(MessageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                String message = "Message-" + i;
                queue.produce(message);
                Thread.sleep((int) (Math.random() * 1000)); // Simulate variable production time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final MessageQueue queue;

    public Consumer(MessageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = queue.consume();
                Thread.sleep((int) (Math.random() * 1500)); // Simulate variable processing time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class DistributedMessageQueueSystem {
    public static void main(String[] args) {
        int queueCapacity = 5;
        int numProducers = 2;
        int numConsumers = 3;

        MessageQueue queue = new MessageQueue(queueCapacity);

        ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);

        // Start producers
        for (int i = 0; i < numProducers; i++) {
            executor.execute(new Producer(queue));
        }

        // Start consumers
        for (int i = 0; i < numConsumers; i++) {
            executor.execute(new Consumer(queue));
        }

        executor.shutdown(); // Shutdown the executor after use
    }
}
