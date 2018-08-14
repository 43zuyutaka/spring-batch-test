package helloxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class FoodItemProcessor implements ItemProcessor<Food, Food> {

	private static final Logger log = LoggerFactory.getLogger(FoodItemProcessor.class);
	
	@Override
	public Food process(final Food food) throws Exception {
		
		final String foodName = food.getFoodName().toUpperCase();
		final String lovely = food.getLovely().toUpperCase();
		
		final Food newFood = new Food(foodName, lovely);
		
		log.info("before="+ food + " -> " + newFood);
		
		return newFood;
	}

}
