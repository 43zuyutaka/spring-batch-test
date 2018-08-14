package helloxml;

public class Food {

	private String foodName;
	private String lovely;

	public Food() {
		
	}
	public Food(String foodName, String lovely) {
		this.foodName = foodName;
		this.lovely = lovely;
	}
	
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	public String getLovely() {
		return lovely;
	}
	public void setLovely(String lovely) {
		this.lovely = lovely;
	}
	
	@Override
	public String toString() {
		return "foodName:" + foodName + ", lovely:" + lovely;		
	}
}
