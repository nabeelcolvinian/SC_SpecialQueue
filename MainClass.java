import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Main Class has been used like a spring context in which object will be created and dependencies will be added
 * TestCases have been added in the main method itself
 * @author user
 *
 */
public class MainClass {
	
	
	/**
	 * Below test cases have been written keeping in mind the functionality that was requested.
	 * Any test case failure will throw a RunTimeException.
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Function<String, Integer> stringToIntFunction = new StringToIntFunction();
		Function<Integer, String> intToStringFunction = new IntToStringFunction();
		
		List<String> listString = Arrays.asList("123", "123", "111", "111", "123", "111");
		
		//Testing sequential addition
		SpecialQueue<String, Integer> queue = new SpecialQueueImpl<String, Integer>(stringToIntFunction);
		listString.stream().forEach(s -> queue.offer(stringToIntFunction.apply(s)));
		System.out.println(queue);
		
		//enable assertions to make it work
		assert(queue.size() == 3);
		
		if(queue.size() != 2) {
			throw new RuntimeException();
		}
		
		queue.removeByKey("123");
		System.out.println(queue);
		if(queue.size() != 1) {
			throw new RuntimeException();
		}
		
		queue.remove(Integer.valueOf("111"));
		System.out.println(queue);
		if(queue.size() != 0) {
			throw new RuntimeException();
		}
		
		//executing tryTake on an empty
		if(queue.tryTake() != null) {
			throw new RuntimeException();
		}
		
		//adding in parallel
		listString.parallelStream().forEach(s -> queue.offer(stringToIntFunction.apply(s)));
		System.out.println(queue);
		
		System.out.println("Removing head of the queue with take() : " + queue.take());
		if(queue.size() != 1) {
			throw new RuntimeException();
		}
		
		System.out.println(queue);
		
		System.out.println("Removing head of the queue with poll() : " + queue.poll(5, TimeUnit.SECONDS));
		if(queue.size() != 0) {
			throw new RuntimeException();
		}
		
		System.out.println(queue);
		
		System.out.println("Removing from a queue with no elements -- poll() :, should return null ");
		if( queue.poll(5, TimeUnit.SECONDS) != null ) {
			throw new RuntimeException();
		}
		
		if(queue.size() != 0) {
			throw new RuntimeException();
		}
		
		System.out.println(queue);
		
	}
}
