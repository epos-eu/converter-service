package org.epos.converter.common.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaExceptionUtil {
	
	@FunctionalInterface
	public interface Consumer_WithExceptions<T> {
		void accept(T t) throws Exception;
	}
	
	@FunctionalInterface
	public interface BiConsumer_WithExceptions<T, U> {
		void accept(T t, U u) throws Exception;
	}
	
	/*	@FunctionalInterface
	public interface Consumer_WithExceptions<T, E extends Exception> {
		void accept(T t) throws E;
	}*/
	
	@FunctionalInterface
	public interface IntConsumer_WithExceptions {
		void accept(Integer t) throws Exception;
	}
	
	@FunctionalInterface
	public interface Supplier_WithExceptions<T> {
		T get() throws Exception;
	}

	@FunctionalInterface
	public interface Function_WithExceptions<T, R> {
	  R apply(T t) throws Exception;
	}
	
	@FunctionalInterface
	public interface IntFunction_WithExceptions<R> {
	  R apply(int value) throws Exception;
	}
		
	@FunctionalInterface
	public interface BiFunction_WithExceptions<T, U, R> {
		R apply(T t, U u) throws Exception;
	}

	@FunctionalInterface
	public interface Predicate_WithExceptions<T> {
	  boolean test(T t) throws Exception;
	}
	  
/*	public static <T, E extends Exception> Consumer<T> consumerRethrower(Consumer_WithExceptions<T, E> consumer, Class<E> exClass) {
	  return t -> {
	    try {
	    	consumer.accept(t);
	    } catch (Exception ex) {
	    	try {
	    		exClass.cast(ex);	    		
	    	} catch (ClassCastException ccEx) {
	    		throw new RuntimeException(ex);
	    	}
	    	sneakyThrow(ex);
	    }
	  };
	}*/
	
	public static IntConsumer intConsumerWrapper(IntConsumer_WithExceptions consumer) {
	    return t -> {
	      try {
	        consumer.accept(t);
	      } catch (Exception ex) {
	        sneakyThrow(ex);
	      }
	    };
	  }
	
	public static <T, U> BiConsumer<T, U> rethrowBiConsumer(BiConsumer_WithExceptions<T, U> consumer) {
		return (t, u) -> {
			try {
		    	consumer.accept(t, u);
		    } catch (Exception ex) {
		    	sneakyThrow(ex);
		    }
		};
	}
	
	public static <T> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T> consumer) {
		return t -> {
			try {
		    	consumer.accept(t);
		    } catch (Exception ex) {
		    	sneakyThrow(ex);
		    }
		};
	}
	
	public static <T> Supplier<T> rethrowSupplier(Supplier_WithExceptions<T> supplier) {
		return () -> {
			  try {
				  return supplier.get();
			  } catch (Exception ex) {
				  sneakyThrow(ex);
				  return null;
			  }
		};
	}
		
	  public static <T, R> Function<T, R> rethrowFunction(Function_WithExceptions<T, R> function) {
	    return t -> {
	      try {
	        return function.apply(t);
	      } catch (Exception ex) {
	        sneakyThrow(ex);
	        return null;
	      }
	    };
	  }
	  
	  public static <R> IntFunction<R> rethrowIntFunction(IntFunction_WithExceptions<R> function) {
		    return t -> {
		      try {
		        return function.apply(t);
		      } catch (Exception ex) {
		        sneakyThrow(ex);
		        return null;
		      }
		    };
		  }
	  
	  
	  public static <T, U, R> BiFunction<T, U, R> rethrowBiFunction(BiFunction_WithExceptions<T, U, R> function) {
		  return (t, u) -> {
			  try {
				  return function.apply(t, u);
			  } catch (Exception ex) {
				  sneakyThrow(ex);
				  return null;
			  }
		  };
	  }
	  
	  
	  public static <T> Predicate<T> predicateWrapper(Predicate_WithExceptions<T> function) {
	    return t -> {
	      try {
	        return function.test(t);
	      } catch (Exception ex) {
	        sneakyThrow(ex);
	        return false;
	      }
	    };
	  }
	 
	  @SuppressWarnings("unchecked")
	  public static <E extends Throwable> void sneakyThrow(Exception ex) throws E {
		  throw (E) ex;
	  }
	  
	  public static <T, U extends Exception> void raiseIssueIf(T obj, Predicate<T> p, Function<String, U> exFunc, String errMsg) {
		  if (p.test(obj)) {
			  raiseIssue(exFunc, errMsg);
		  }
	  }
		
	  public static <T, U extends Exception> void raiseIssue(Function<String, U> exFunc, String errMsg) {
		  sneakyThrow(exFunc.apply(errMsg));
	  }

}
