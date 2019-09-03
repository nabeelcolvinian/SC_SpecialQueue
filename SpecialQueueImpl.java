import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Intended to be used as a singleton bean in a spring context which can be autowired in any class.
 * 
 * @author user
 *
 * @param <K>
 * @param <E>
 */
public class  SpecialQueueImpl<K,E> implements SpecialQueue<K, E>{
	
	private Function<K,E> function;
	private LinkedBlockingQueue<Entry<E>> queue;
	
	//@Autowired in a spring context
	public SpecialQueueImpl(Function<K,E> function) {
		this.function = function;
		this.queue = new LinkedBlockingQueue<Entry<E>>();
	}
	@Override
	public int size() {
		return queue.size();
	}

	/**
	 * takes an element(this) and if an equal object(that) exist then this object replace that object.
	 * inner class has been created to keep the priority of the entries.
	 */
	@Override
	public void offer(E e) {
		if(e==null) {
			throw new NullPointerException();// not allowing null value
		}
		synchronized (queue) {
			Entry<E> en = new SpecialQueueImpl.Entry<E>(e);
			if(queue.contains(en)) {// checking in a synchronized context to read the correct value 
				queue.stream().filter(entry -> entry.getE().equals(e)).limit(1).findAny().ifPresent( entry -> entry.setE(e) );
			} else {
				queue.add(en);
			}
		}
	}
	

	@Override
	public boolean removeByKey(K key) {
		return queue.remove(new Entry<E>(function.apply(key)));
	}

	@Override
	public Boolean remove(E e) {
		return queue.remove(new Entry<E>(e));
	}

	@Override
	public E take() throws InterruptedException {
		return queue.take().getE();
	}

	@Override
	public E tryTake() {
		return (E) Optional.ofNullable(queue.poll()).orElse(new Entry<E>(null)).getE();
	}

	/**
	 * this method should ideally return an Optional<E> as the value may not be available during the Timeout.
	 * but since the method return type has been given ,this method will return a null if the value is not available after the timeout
	 */
	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		 return Optional.ofNullable(queue.poll(timeout, unit)).orElseGet(() -> new Entry<E>(null)).getE();
	}

	
	@Override
	public String toString() {
		return queue.stream().map(e -> e.getE().toString()).collect(Collectors.joining(","));
	}


	static class Entry<E> {
		private E e;
		
		Entry (E e){
			this.e = e;
		}
		public E getE() {
			return e;
		}
		
		public void setE(E e) {
			this.e = e;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result ;
			result = prime * result + ((e == null) ? 0 : e.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Entry<E> other = (Entry<E>) obj;
			return this.getE().equals(other.getE());
		}
		
	}
	
}

