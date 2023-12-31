package com.hotel.user;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import org.springframework.jdbc.core.RowMapper;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {


    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private String nutritionServiceBaseURL = "http://localhost:8080/";
    private String exerciseServiceBaseURL = "http://localhost:3005/";
    private String userServiceBaseURL = "http://localhost:8081/";

    WebClient exercise = WebClient.create(exerciseServiceBaseURL);

    UserController(UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello, this is a test for User!";
    }


    @GetMapping("/getAllUsers") 
    public List<User> user() {
        String query = """
                    SELECT *
                    FROM `userInfo`
                """;
        return jdbcTemplate.query(query, new UserMapper());
    }

    @GetMapping("/AverageCaloriesConsumed")
    public double getAvgCaloriesConsumed(@RequestParam String email, @RequestParam String startDate, @RequestParam String endDate) {
    String sqlQuery = "SELECT (SELECT COALESCE(SUM(calories), 0) FROM users.foodLogs WHERE email = ? AND dateAdded BETWEEN ? AND ?) / (SELECT COUNT(DISTINCT dateAdded) FROM users.foodLogs WHERE email = ? AND dateAdded BETWEEN ? AND ?) AS average_calories";

    return jdbcTemplate.queryForObject(sqlQuery, new Object[]{email, startDate, endDate, email, startDate, endDate}, Double.class);


}

@PostMapping("/WeightLossFunction")
 public double WeightLossFunction(@RequestParam String email,
                                     @RequestParam String endDate,
                                     @RequestParam int currentWeight,
                                     @RequestParam int averageCalorieIntake,
                                     @RequestParam int BMR,
                                     @RequestParam int averageCalsBurnt) {
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        long daysUntilTarget = ChronoUnit.DAYS.between(LocalDate.now(), endLocalDate);

        
        int dailyDietDeficit = BMR - averageCalorieIntake;

        
        double dailyExerciseCalories = averageCalsBurnt;

        
        double totalDailyDeficit = dailyDietDeficit + dailyExerciseCalories;

        
        double totalCalorieDeficit = totalDailyDeficit * daysUntilTarget;

        
        double estimatedWeightLoss = totalCalorieDeficit / 3500.0;

        
        return currentWeight - estimatedWeightLoss;
    }

    
    @PostMapping("/user/AddNewUser")
    public ResponseEntity<Object> addUser(@RequestParam String email, @RequestParam String password, @RequestParam String First_name, @RequestParam String last_name, @RequestParam int Age,
                                          @RequestParam String Sex, @RequestParam int weight, @RequestParam int height) {

        String specialCharacter = ".*[^a-z0-9 ].*";
        String number = ".*[0-9].*";
        String uppercase = ".*[A-Z].*";
        if (password.length() < 8 || !password.matches(specialCharacter)
                || !password.matches(number) || !password.matches(uppercase)) {
            
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        jdbcTemplate.update(
                "INSERT INTO users.userInfo (email, password, firstName, lastName, age, sex, weight, height) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                email, password, First_name, last_name, Age, Sex, weight, height
        );
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/user/addUser") 
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        String apiUrl = userServiceBaseURL + "user/" + user.getUserId();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

        
        String password = user.getPassword();
        String specialCharacter = ".*[^a-z0-9 ].*";
        String number = ".*[0-9].*";
        String uppercase = ".*[A-Z].*";
        if (password.length() < 8 || !password.matches(specialCharacter)
                || !password.matches(number) || !password.matches(uppercase)) {
            //can return more info abt whats spefically wrong when refactor
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String query = """
                    INSERT INTO `userInfo` (email, password, firstName, lastName, age, sex, weight, height)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(query, user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getAge(), user.getSex(), user.getWeight(), user.getHeight());

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    
    @GetMapping("/getUserEmail")
    public User userByEmail(@RequestParam String email) {
        String query = """
                    SELECT *
                    FROM `userInfo`
                    WHERE email LIKE '%%%s%%'
                """;
        String SQL = String.format(query, email);

        User temp = jdbcTemplate.queryForObject(SQL, new UserMapper());
        return temp;
    }

    @GetMapping("/CalculateBMR")
public double calculateBmrForEmail(String email) {
    String sql = "SELECT age, sex, weight, height FROM users.userInfo WHERE email = ?";

    RowMapper<Double> rowMapper = new RowMapper<Double>() {
        public Double mapRow(ResultSet rs, int rowNum) throws SQLException {
            int age = rs.getInt("age");
            String sex = rs.getString("sex");
            double weight = rs.getDouble("weight"); 
            double height = rs.getDouble("height"); 

           
            double weightInKg = weight * 0.453592;
            double heightInCm = height * 2.54;

            
            if ("male".equalsIgnoreCase(sex)) {
                return 88.362 + (13.397 * weightInKg) + (4.799 * heightInCm) - (5.677 * age);
            } else if ("female".equalsIgnoreCase(sex)) {
                return 447.593 + (9.247 * weightInKg) + (4.799 * heightInCm) - (5.677 * age);
            } else {
                throw new IllegalArgumentException("Invalid sex value: " + sex);
            }
        }
    };


    Double bmr = jdbcTemplate.queryForObject(sql, new Object[]{email}, rowMapper);
    return bmr;
    
}

    
    @GetMapping("/user/id/{id}")
    public List<User> userById(@PathVariable String id) {
        String query = """
                    SELECT *
                    FROM `userInfo`
                    WHERE userId LIKE '%%%s%%'
                """;
        String SQL = String.format(query, id);

        List<User> temp = jdbcTemplate.query(SQL, new UserMapper());
        return temp;
    }

    //delete user by email
    @DeleteMapping("/user/deleteUser/{email}")
    public ResponseEntity<Object> deleteUser(@RequestBody User user) {
        String query = """
                    DELETE FROM `userInfo`
                    WHERE email = ?
                """;
        jdbcTemplate.update(query, user.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    

    @GetMapping("/activityfactor")
    private double getTotalCaloriesBurntFromExercise(@RequestParam String email) {
        String sql = "SELECT SUM(caloriesBurnt) / " +
                     "(SELECT COUNT(DISTINCT CAST(date AS DATE)) FROM exercise.exercise_log WHERE email = ?) " +
                     "AS averageCalories FROM exercise.exercise_log WHERE email = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{email, email}, Double.class);
    }

    @PostMapping("/user/AddEntry") // only works if user exists
    public void addIngredientEntry(@RequestBody List<FoodLog> foodLogs) {
        // Assuming all FoodLog objects have the same email, date, and mealType
        FoodLog foodLogFinal = new FoodLog();
        foodLogFinal.setEmail(foodLogs.get(0).getEmail());
        foodLogFinal.setDate(foodLogs.get(0).getDate());
        foodLogFinal.setMealType(foodLogs.get(0).getMealType());

        // Initialize sums
        int totalServings = 0;
        int totalCalories = 0;
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFat = 0;
        StringBuilder foodNames = new StringBuilder();

        
        for (FoodLog log : foodLogs) {
            totalServings += log.getServings();
            totalCalories += log.getCalories();
            totalProtein += log.getProtein();
            totalCarbs += log.getCarbs();
            totalFat += log.getFat();
            if (foodNames.length() > 0) {
                foodNames.append(", ");
            }
            foodNames.append(log.getFoodName());
        }

        
        foodLogFinal.setFoodName(foodNames.toString());
        foodLogFinal.setServings(totalServings);
        foodLogFinal.setCalories(totalCalories);
        foodLogFinal.setProtein(totalProtein);
        foodLogFinal.setCarbs(totalCarbs);
        foodLogFinal.setFat(totalFat);

        String SQL = "INSERT INTO users.foodLogs (email, foodName, dateAdded, servings, calories, protein, carbs, fat, mealType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, foodLogFinal.getEmail(), foodLogFinal.getFoodName(), foodLogFinal.getDate(), foodLogFinal.getServings(), foodLogFinal.getCalories(), foodLogFinal.getProtein(), foodLogFinal.getCarbs(), foodLogFinal.getFat(), foodLogFinal.getMealType());
    }

    @GetMapping("/confirm-user")
    public int confirmLogin(@RequestParam String email, @RequestParam String password) {
        String SQL = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS IsMatch FROM users.userInfo WHERE email = ? AND password = ?";
        int isMatch = jdbcTemplate.queryForObject(SQL, new Object[]{email, password}, Integer.class);
        return isMatch;
    }


    @GetMapping("pull-diet-log")
    public List<FoodLog> pullPreviousLogs(@RequestParam String email) {
        String SQL = "SELECT * FROM users.foodLogs WHERE email = ?";
        return jdbcTemplate.query(SQL, new Object[]{email}, new RowMapper<FoodLog>() {
            @Override
            public FoodLog mapRow(ResultSet rs, int rowNum) throws SQLException {
                FoodLog log = new FoodLog();
                log.setFoodName(rs.getString("foodName"));
                log.setEmail(rs.getString("email"));
                log.setDate(rs.getString("dateAdded"));
                log.setServings(rs.getInt("servings"));
                log.setCalories(rs.getInt("calories"));
                log.setProtein(rs.getInt("protein"));
                log.setCarbs(rs.getInt("carbs"));
                log.setFat(rs.getInt("fat"));
                log.setMealType(rs.getString("mealType"));
                return log;
            }
        });
    }

    @GetMapping("/get-nutrition")
    public FoodLog getNutritionValues(@RequestParam String email, @RequestParam String food, @RequestParam int quantity, @RequestParam String mealType, @RequestParam String date) {
        FoodLog foodlog = new FoodLog();
        System.out.println("NUMBER: " + quantity);
        String SQLCalories = "SELECT `calories` FROM cnf.sampleFoods WHERE `name` = ?";
        int calorieCount = (int) (jdbcTemplate.queryForObject(SQLCalories, new Object[]{food}, Integer.class) * ((double) quantity / 100));
        System.out.println("NUMBER: " + calorieCount);
        String SQLProtein = "SELECT `protein` FROM cnf.sampleFoods WHERE `name` = ?";
        int proteincount = (int) (jdbcTemplate.queryForObject(SQLProtein, new Object[]{food}, Integer.class) * ((double) quantity / 100));
        String SQLCarbs = "SELECT `carbs` FROM cnf.sampleFoods WHERE `name` = ?";
        int carbcount = (int) (jdbcTemplate.queryForObject(SQLCarbs, new Object[]{food}, Integer.class) * ((double) quantity / 100));
        String SQLfat = "SELECT `fat` FROM cnf.sampleFoods WHERE `name` = ?";
        int fatcount = (int) (jdbcTemplate.queryForObject(SQLfat, new Object[]{food}, Integer.class) * ((double) quantity / 100));
        foodlog.setCalories(calorieCount);
        foodlog.setFoodName(food);
        foodlog.setProtein(proteincount);
        foodlog.setCarbs(carbcount);
        foodlog.setFat(fatcount);
        foodlog.setServings(quantity);
        foodlog.setEmail(email);
        foodlog.setDate(date);
        foodlog.setMealType(mealType);
        return foodlog;
    }

    @GetMapping("check-multiple-mealtype")
    public int checkMultipleMeals(@RequestParam String email, @RequestParam String date, @RequestParam String mealType) {
        String SQL = "SELECT CASE \n" + //
                "         WHEN EXISTS(SELECT 1 \n" + //
                "                     FROM users.foodLogs \n" + //
                "                     WHERE email = ? \n" + //
                "                     AND dateAdded = ? \n" + //
                "                     AND mealType != 'snack'\n" + //
                "                     AND mealType = ?) \n" + //
                "         THEN 1 \n" + //
                "         ELSE 0 \n" + //
                "       END AS MealExists;\n" + //
                "";
        int check = jdbcTemplate.queryForObject(SQL, new Object[]{email, date, mealType}, Integer.class);
        return check;
    }

    @GetMapping("/visualize-top-5")
    public List<Map<String, Object>> visualizeTop5Nutrients(@RequestParam String email, @RequestParam String startDate, @RequestParam String endDate) {
        String SQL = "SELECT SUM(protein) AS total_protein, SUM(carbs) AS total_carbs, SUM(fat) AS total_fat, SUM(calories) AS total_kcal FROM users.foodLogs WHERE email = '" + email + "' AND dateAdded BETWEEN '" + startDate + "' AND '" + endDate + "'";
        return jdbcTemplate.queryForList(SQL);
    }




    public static int[] parseNutritionInfo(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        if (jsonNode.isArray() && jsonNode.size() > 0) {
            JsonNode firstItem = jsonNode.get(0);

            int calories = firstItem.get("calories").asInt();
            int protein = firstItem.get("protein").asInt();
            int carbs = firstItem.get("carbs").asInt();
            int fat = firstItem.get("fat").asInt();

            return new int[]{calories, protein, carbs, fat};
        } else {
            throw new IllegalArgumentException("Invalid JSON format");
        }
    }

    @PostMapping("/updateInfo")
    public void updateInfo(@RequestBody User user) {
        String sql = "UPDATE userInfo SET password = ?, firstName = ?, lastName = ?, age = ?, sex = ?, weight = ?, height = ? WHERE email = ?";
        jdbcTemplate.update(sql, new Object[]{user.getPassword(), user.getFirstName(), user.getLastName(), user.getAge(), user.getSex(), user.getWeight(), user.getHeight(), user.getEmail()});
    }



    @GetMapping("/pull-exercise-log")
    public List<ExerciseLog> pullExerciseLogs(@RequestParam String email) {
        String SQL = "SELECT * FROM exercise.exercise_log WHERE email = ?";
        return jdbcTemplate.query(SQL, new Object[]{email}, new RowMapper<ExerciseLog>() {
            @Override
            public ExerciseLog mapRow(ResultSet rs, int rowNum) throws SQLException {
                ExerciseLog log = new ExerciseLog();
                log.setEmail(rs.getString("email"));
                log.setDate(rs.getDate("date").toString()); // Ensure this matches your ExerciseLog class date field type
                log.setDuration(rs.getInt("length"));
                log.setExerciseType(rs.getString("type"));
                log.setIntensity(rs.getString("intensity"));
                log.setCaloriesBurned(rs.getInt("caloriesBurnt"));
                return log;
            }
        });
    }
}