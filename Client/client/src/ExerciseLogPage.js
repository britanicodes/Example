import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const BMR = 1500; // Replace with real calculation

function ExerciseLogPage() {
  let [logs, setLogs] = useState([]);
  let [newLog, setNewLog] = useState({
    date: '',
    time: '',
    duration: '',
    exerciseType: '',
    intensity: ''
  });
  const [searchDate, setSearchDate] = useState('');

  const navigate = useNavigate();

  const goBack = () => {
    navigate('/options');
  };

  const handleInputChange = (e) => {
    setNewLog({ ...newLog, [e.target.name]: e.target.value });
  };

  const addLog = () => {
    const { date, time, duration, exerciseType, intensity } = newLog;

    if (!date || !time || !duration || !exerciseType || !intensity) {
      alert('Please enter all fields before submitting.');
      return;
    }

    addData(window.emailGlobalVar, duration, exerciseType, date, intensity, newLog, setLogs, logs, setNewLog);

    // Check for overlapping workouts
    const startTime = new Date(`${date}T${time}`);
    const endTime = new Date(startTime.getTime() + duration * 60000);
    const isOverlapping = logs.some(log => {
      const logStartTime = new Date(`${log.date}T${log.time}`);
      const logEndTime = new Date(logStartTime.getTime() + log.duration * 60000);
      return (startTime < logEndTime && endTime > logStartTime);
    });

    if (isOverlapping) {
      alert('This workout overlaps with an existing workout.');
      return;
    }

  };

  const deleteLog = (index) => {
    const updatedLogs = logs.filter((_, logIndex) => logIndex !== index);
    setLogs(updatedLogs);
  };

  const getFilteredLogs = () => {
    return searchDate ? logs.filter(log => log.date === searchDate) : logs;
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'flex-start', justifyContent: 'center', height: '100vh', padding: '20px' }}>
      <button className="btn btn-outline-primary" onClick={goBack}>
            Back
          </button>

      {/* Exercise Log Entry Column */}
      <div style={{ width: '40%', marginRight: '20px', padding: '20px', borderRadius: '10px', boxShadow: '0px 0px 10px #ccc', textAlign: 'center' }}>
        <h2 style={{ color: '#333' }}>Add Exercise Log</h2>
        <form onSubmit={(e) => e.preventDefault()} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <input type="date" name="date" value={newLog.date} onChange={handleInputChange} style={{ padding: '10px', marginBottom: '10px', borderRadius: '10px', border: '1px solid #ccc', width: '100%' }} />
          <input type="time" name="time" value={newLog.time} onChange={handleInputChange} style={{ padding: '10px', marginBottom: '10px', borderRadius: '10px', border: '1px solid #ccc', width: '100%' }} />
          <input type="number" name="duration" value={newLog.duration} placeholder="Duration (minutes)" onChange={handleInputChange} min="1" style={{ padding: '10px', marginBottom: '10px', borderRadius: '10px', border: '1px solid #ccc', width: '100%' }} />
          <select name="exerciseType" value={newLog.exerciseType} onChange={handleInputChange} style={{ padding: '10px', marginBottom: '10px', borderRadius: '10px', border: '1px solid #ccc', width: '100%' }}>
            <option value="">Select Exercise Type</option>
            <option value="running">Running</option>
            <option value="walking">Walking</option>
            <option value="swimming">Swimming</option>
            <option value="weightLifting">Weightlifting</option>
          </select>
          <select name="intensity" value={newLog.intensity} onChange={handleInputChange} style={{ padding: '10px', marginBottom: '10px', borderRadius: '10px', border: '1px solid #ccc', width: '100%' }}>
            <option value="">Select Intensity</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
          </select>
          <button onClick={addLog} style={{ backgroundColor: 'green', color: 'white', borderRadius: '20px', padding: '10px 20px', border: 'none' }}>Log Exercise</button>
        </form>
      </div>

      {/* Exercise Log Display Column */}
      <div style={{ width: '40%', padding: '20px', borderRadius: '10px', boxShadow: '0px 0px 10px #ccc', textAlign: 'center', overflowY: 'auto', maxHeight: '80vh' }}>
        <h2 style={{ color: '#333', marginBottom: '20px' }}>Previous Logs</h2>
        <input type="date" value={searchDate} onChange={(e) => setSearchDate(e.target.value)} style={{ padding: '10px', marginBottom: '20px', borderRadius: '20px', border: '1px solid #ccc', width: '100%' }} />
        <ul style={{ listStyle: 'none', paddingLeft: '0' }}>
          {getFilteredLogs().map((log, index) => (
            <li key={index} style={{ marginBottom: '10px', padding: '10px', border: '1px solid #ccc', borderRadius: '10px' }}>
              <div><strong>Date:</strong> {log.date}</div>
              <div><strong>Time:</strong> {log.time}</div>
              <div><strong>Duration:</strong> {log.duration} minutes</div>
              <div><strong>Exercise Type:</strong> {log.exerciseType}</div>
              <div><strong>Intensity:</strong> {log.intensity}</div>
              <div><strong>Calories Burned:</strong> {log.caloriesBurned}</div>
              <div><strong>BMR:</strong> {log.BMR}</div>
              <button onClick={() => deleteLog(index)} style={{ backgroundColor: 'red', color: 'white', borderRadius: '20px', padding: '5px 10px' }}>Delete Log</button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
export async function addData(email, duration, exerciseType, date, intensity, newLog, setLogs, logs, setNewLog){
  
  var requestOptions = {
    method: 'POST',
    redirect: 'follow'
  };

  await fetch("http://localhost:3005/LogExercise?email="+email+"&type="+exerciseType+"&length="+duration+"&date="+date+"&intensity="+intensity, requestOptions)
    .then(response => response.text())
    .then(result => console.log(result))
    .catch(error => console.log('error', error));

  var requestOptions = {
    method: 'GET',
    redirect: 'follow'
  };
  
  await fetch("http://localhost:3005/CaloriesBurntOn?email="+email+"&day="+date, requestOptions)
    .then(response => response.text())
    .then(result => {
      console.log(result.toString());
      let newExerciseLog = {
        ...newLog,
        caloriesBurned: result,
        BMR
      };
  
      setLogs([...logs, newExerciseLog]);
      setNewLog({
        date: '',
        time: '',
        duration: '',
        exerciseType: '',
        intensity: ''
      });
    })
    .catch(error => console.log('error', error));

}
export default ExerciseLogPage;
