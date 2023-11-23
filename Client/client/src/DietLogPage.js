import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function DietLogPage() {
  const [logs, setLogs] = useState([]);
  const [newLog, setNewLog] = useState({
    date: '',
    mealType: '',
    ingredients: [{ name: '', quantity: '' }]
  });

    
  const [searchDate, setSearchDate] = useState('');

  const navigate = useNavigate();

  const goBack = () => {
    navigate(-1);
  };

  const handleInputChange = (e, index, field) => {
    if (index === -1) {
      setNewLog({ ...newLog, [field]: e.target.value });
    } else {
      const newIngredients = [...newLog.ingredients];
      newIngredients[index] = { ...newIngredients[index], [field]: e.target.value };
      setNewLog({ ...newLog, ingredients: newIngredients });
    }
  };

  const addIngredientField = () => {
    setNewLog({
      ...newLog,
      ingredients: [...newLog.ingredients, { name: '', quantity: '' }]
    });
  };

  const removeIngredientField = (index) => {
    const newIngredients = newLog.ingredients.filter((_, i) => i !== index);
    setNewLog({ ...newLog, ingredients: newIngredients });
  };

  const addLog = () => {
    if (!newLog.date || !newLog.mealType || newLog.ingredients.some(ingredient => !ingredient.name || !ingredient.quantity)) {
      alert('Please enter date, meal type, and at least one ingredient with quantity.');
      return;
    }
  
    if (newLog.mealType !== 'snack') {
      const existingLog = logs.find(log => log.date === newLog.date && log.mealType === newLog.mealType);
      if (existingLog) {
        alert(`A log for ${newLog.mealType} on this date already exists.`);
        return;
      }
    }
  
    const firstIngredient = newLog.ingredients[0];
    const email = window.emailGlobalVar;
    const food = encodeURIComponent(firstIngredient.name);
    const quantity = firstIngredient.quantity;
    const mealType = encodeURIComponent(newLog.mealType);
    const date = encodeURIComponent(newLog.date);
  
    const url = `http://localhost:8081/get-nutrition?email=${email}&food=${food}&quantity=${quantity}&mealType=${mealType}&date=${date}`;
  
    var requestOptions = {
      method: 'POST',
      redirect: 'follow'
    };
  
    fetch(url, requestOptions)
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    // Check if the headers object exists and if 'Content-Type' header is present
    let contentType = response.headers && response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return response.json(); // This will parse the response as JSON
    } else {
      return response.text(); // If there's no content-type, fallback to text
    }
  })
  .then(data => {
    // Handle data processing here
    // ...
  })
  .catch(error => {
    console.error('Fetch error:', error);
    alert('Failed to fetch nutrition data. Please try again.');
  });

  };
  
  

  const deleteLog = (index) => {
    const updatedLogs = logs.filter((_, logIndex) => logIndex !== index);
    setLogs(updatedLogs);
  };

  const getFilteredLogs = () => {
    return searchDate ? logs.filter(log => log.date === searchDate) : logs;
  };

  const ingredientsList = ["Apple", "Banana", "Strawberries", "Watermelon", "Avocado", "Spinach (raw)", "Cheese (Cheddar)", "WW Bread", "Cottage Cheese", "Salad Greens", "Almonds", "Oatmeal", "Egg (Boiled)", "Black Beans", "Broccoli (Steamed)", "Greek Yogurt", "Quinoa", "Chicken Breast", "Salmon (Baked)", "Sweet Potato", "Brown Rice"];
//place holder values
  const placeholderNutrition = {
    calories: 250, // Placeholder value
    proteins: 10,  // Placeholder value
    carbs: 30,     // Placeholder value
    fats: 8        // Placeholder value
  };



  

  return (
    <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'flex-start', justifyContent: 'center', height: '100vh', padding: '20px' }}>
      <button onClick={goBack} style={{ position: 'absolute', top: '20px', left: '20px', padding: '10px 20px', backgroundColor: 'lightgray', borderRadius: '20px' }}>Back</button>
      
      {/* Log Entry Column */}
      <div style={{ width: '40%', marginRight: '20px', padding: '20px', borderRadius: '10px', boxShadow: '0px 0px 10px #ccc', textAlign: 'center' }}>
        <h2 style={{ color: '#333' }}>Add Diet Log</h2>
        <form onSubmit={(e) => e.preventDefault()} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <input
            type="date"
            name="date"
            value={newLog.date}
            onChange={(e) => handleInputChange(e, -1, 'date')}
            style={{ padding: '10px', marginBottom: '10px', borderRadius: '10px', border: '1px solid #ccc', width: '100%' }}
          />
          <select
            name="mealType"
            value={newLog.mealType}
            onChange={(e) => handleInputChange(e, -1, 'mealType')}
            style={{ padding: '10px', marginBottom: '10px', borderRadius: '10px', border: '1px solid #ccc', width: '100%' }}
          >
            <option value="">Select Meal Type</option>
            <option value="Breakfast">Breakfast</option>
            <option value="Lunch">Lunch</option>
            <option value="Dinner">Dinner</option>
            <option value="Snack">Snack</option>
          </select>
          {/* Ingredients List */}
          {newLog.ingredients.map((ingredient, index) => (
            <div key={index} style={{ display: 'flex', gap: '5px', alignItems: 'center' }}>
              <select
                name="name"
                value={ingredient.name}
                onChange={(e) => handleInputChange(e, index, 'name')}
                style={{ padding: '5px', flexGrow: 1, borderRadius: '10px', border: '1px solid #ccc' }}
              >
                <option value="">Select Ingredient</option>
                {ingredientsList.map((ingredientName) => (
                  <option key={ingredientName} value={ingredientName}>{ingredientName}</option>
                ))}
              </select>
              <input
                type="number"
                name="quantity"
                value={ingredient.quantity}
                placeholder="Quantity (grams)"
                onChange={(e) => handleInputChange(e, index, 'quantity')}
                min="1"
                style={{ padding: '5px', flexGrow: 1, borderRadius: '10px', border: '1px solid #ccc' }}
              />
              <button onClick={() => removeIngredientField(index)} style={{ backgroundColor: 'red', color: 'white', borderRadius: '20px', padding: '5px 10px' }}>X</button>
            </div>
          ))}
          <button onClick={addIngredientField} style={{ backgroundColor: 'blue', color: 'white', borderRadius: '20px', padding: '10px 20px', border: 'none' }}>Add Ingredient</button>
          <button onClick={addLog} style={{ backgroundColor: 'green', color: 'white', borderRadius: '20px', padding: '10px 20px', border: 'none' }}>Log Diet</button>
          
          
          
        </form>
      </div>

      {/* Log Display Column */}
      <div style={{ width: '40%', padding: '20px', borderRadius: '10px', boxShadow: '0px 0px 10px #ccc', textAlign: 'center', overflowY: 'auto', maxHeight: '80vh' }}>
        <h2 style={{ color: '#333', marginBottom: '20px' }}>Previous Logs</h2>
        <input
          type="date"
          value={searchDate}
          onChange={(e) => setSearchDate(e.target.value)}
          style={{ padding: '10px', marginBottom: '20px', borderRadius: '20px', border: '1px solid #ccc', width: '100%' }}
        />
        <ul style={{ listStyle: 'none', paddingLeft: '0' }}>
          {getFilteredLogs().map((log, index) => (
            <li key={index} style={{ marginBottom: '10px', padding: '10px', border: '1px solid #ccc', borderRadius: '10px' }}>
              <div><b>Date: </b>{log.date}</div>
              <div><b>Meal: </b>{log.mealType}</div>
              {/* Ingredients List */}
              {log.ingredients.length > 0 && (
                <div>
                  <div><b>Ingredients:</b></div>
                  <ul>
                    {log.ingredients.map((ingredient, ingredientIndex) => (
                      <li key={ingredientIndex}>
                        {ingredient.name} - {ingredient.quantity} grams
                      </li>
                    ))}
                  </ul>
                </div>
              )}
              <div><b>Nutritional Value:</b></div>
              <div>Calories: {placeholderNutrition.calories}</div>
              <div>Proteins: {placeholderNutrition.proteins}</div>
              <div>Carbs: {placeholderNutrition.carbs}</div>
              <div>Fats: {placeholderNutrition.fats}</div>
              <button onClick={() => deleteLog(index)} style={{ backgroundColor: 'red', color: 'white', borderRadius: '20px', padding: '5px 10px' }}>Delete Log</button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default DietLogPage;