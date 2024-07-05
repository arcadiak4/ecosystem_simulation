public class FailSpreadException extends Exception{
	private String message;
	
	public  FailSpreadException(String e){
		this.message = e;
	}
	public String getMessage(){
		return message;
	}
}
	
