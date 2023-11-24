package com.hotel.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
public class UserController {
    
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private String nutritionServiceBaseURL = "http://localhost:8080/";
    private String userServiceBaseURL = "http://localhost:8081/";

    UserController(UserRepository userRepository, JdbcTemplate jdbcTemplate){
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello, this is a test for User!";
    }

    //--------------------------------------------//
    //----------------user methods----------------//
    //--------------------------------------------//
    @GetMapping("/getAllUsers") //get all users in db
    public List<User> user() {
        String query = """
                SELECT *
                FROM `userInfo`
            """;
        return jdbcTemplate.query(query, new UserMapper());
    }
    //Method being used my frontend which arahan made
    @PostMapping("/user/AddNewUser")
    public ResponseEntity<Object> addUser(@RequestParam String email, @RequestParam String password, @RequestParam String First_name, @RequestParam String last_name, @RequestParam int Age, 
    @RequestParam String Sex, @RequestParam int weight, @RequestParam int height){

       String specialCharacter = ".*[^a-z0-9 ].*";
        String number = ".*[0-9].*";
        String uppercase = ".*[A-Z].*";
        if(password.length() < 8 || !password.matches(specialCharacter)
           || !password.matches(number) || !password.matches(uppercase)){
            //can return more info abt whats spefically wrong when refactor
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
      jdbcTemplate.update(
    "INSERT INTO users.userInfo (email, password, firstName, lastName, age, sex, weight, height) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
    email, password, First_name, last_name, Age, Sex, weight, height
    );
    return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/user/addUser") //add user to db (need to add error handling   
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        String apiUrl = userServiceBaseURL + "user/" + user.getUserId();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
        
        //Validate some basic password criteria
        String password = user.getPassword();
        String specialCharacter = ".*[^a-z0-9 ].*";
        String number = ".*[0-9].*";
        String uppercase = ".*[A-Z].*";
        if(password.length() < 8 || !password.matches(specialCharacter)
           || !password.matches(number) || !password.matches(uppercase)){
            //can return more info abt whats spefically wrong when refactor
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String query = """
            INSERT INTO `userInfo` (email, password, firstName, lastName, age, sex, weight, height)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(query, user.getEmail(), user.getPassword(), user.getFirstName(),user.getLastName(), user.getAge(), user.getSex(), user.getWeight(), user.getHeight());

         return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //get user by email and return its info
    @GetMapping("/user/email/{email}") 
    public List<User> userByEmail(@PathVariable String email) {
        String query = """
            SELECT *
            FROM `userInfo`
            WHERE email LIKE '%%%s%%'
        """;
        String SQL = String.format(query, email);

        List<User> temp = jdbcTemplate.query(SQL, new UserMapper());
        return temp;
    }

    //get user by id
     //todo: edit to hide password
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

    //todo: update user by email and password verification 
    
    //-----------------------------------------------//
    //----------------foodlog methods----------------//
    //-----------------------------------------------//
    @PostMapping("/user/AddEntry") //only works if user exists
   public void addIngredientEntry(@RequestBody FoodLog foodlog)                                              
    {
        String SQL = "INSERT INTO users.foodLogs (email, foodName, dateAdded, servings, calories, protein, carbs, fat, mealType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, foodlog.getEmail(), foodlog.getFoodName(), foodlog.getDate(), foodlog.getServings(), foodlog.getCalories(), foodlog.getProtein(), foodlog.getCarbs(), foodlog.getFat(), foodlog.getMealType());
    }

    @GetMapping("/confirm-user")
    public int confirmLogin(@RequestParam String email, @RequestParam String password) {
    String SQL = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS IsMatch FROM users.userInfo WHERE email = ? AND password = ?";
    int isMatch = jdbcTemplate.queryForObject(SQL, new Object[]{email, password}, Integer.class);
    return isMatch;
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
    
    @PostMapping("/get-nutrition")
    public void getNutritionValues(@RequestParam String email, @RequestParam String food, @RequestParam int quantity, @RequestParam String mealType, @RequestParam String date) {
        FoodLog foodlog = new FoodLog();
        String SQLCalories = "SELECT `calories` FROM cnf.sampleFoods WHERE `name` = ?";
        int caloriecount = (jdbcTemplate.queryForObject(SQLCalories, new Object[]{food}, Integer.class))*(quantity/100);
        String SQLProtein = "SELECT `protein` FROM cnf.sampleFoods WHERE `name` = ?";
        int proteincount = jdbcTemplate.queryForObject(SQLProtein, new Object[]{food}, Integer.class)*(quantity/100);
        String SQLCarbs = "SELECT `carbs` FROM cnf.sampleFoods WHERE `name` = ?";
        int carbcount = jdbcTemplate.queryForObject(SQLCarbs, new Object[]{food}, Integer.class)*(quantity/100);
        String SQLfat = "SELECT `fat` FROM cnf.sampleFoods WHERE `name` = ?";
        int fatcount = jdbcTemplate.queryForObject(SQLfat, new Object[]{food}, Integer.class)*(quantity/100);
        foodlog.setCalories(caloriecount);
        foodlog.setFoodName(food);
        foodlog.setProtein(proteincount);
        foodlog.setCarbs(carbcount);
        foodlog.setFat(fatcount);
        foodlog.setServings(quantity);
        foodlog.setEmail(email);
        foodlog.setDate(date);
        foodlog.setMealType(mealType);
        addIngredientEntry(foodlog);
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
    //delete food log entry

    //update food log entry

    //get food log entries by email

    // get food log entries by date 
    
    // get food log entries by date range

    // get food log entries by food name


    //---------------------------------------------------//
    //----------------exerciselog methods----------------//
    //---------------------------------------------------//


    
    //add exercise log entry

    //delete exercise log entry

    //update exercise log entry

    //get exercise log entries by email

    // get exercise log entries by date

    // get exercise log entries by date range

    // get exercise log entries by exercise name

    



}
