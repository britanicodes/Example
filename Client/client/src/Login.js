import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';



function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = (event) => {
    event.preventDefault();

    // Set up the requestOptions object
    const requestOptions = {
      method: 'GET',
      redirect: 'follow'
    };

    // Construct the URL with the user-entered email and password
    const url = `http://localhost:8081/confirm-user?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;

    // Perform the fetch request
    fetch(url, requestOptions)
      .then(response => response.text())
      .then(result => {
        console.log(result);
        // Assuming the result is '1' for a successful login
        if (result === '1') {
          localStorage.setItem('isLoggedIn', 'true');
          window.emailGlobalVar = email;
          navigate('/options');
          
          
        } else {
          alert('Invalid credentials');
        }
      })
      .catch(error => {
        console.log('error', error);
        alert('Login failed. Please try again later.');
      });
  };


  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-4">
          <div className="card">
            <h3 className="card-header text-center">Login</h3>
            <div className="card-body">
              <form onSubmit={handleLogin}>
                <div className="mb-3">
                  <label htmlFor="email" className="form-label">Email:</label>
                  <input
                    type="email"
                    className="form-control"
                    id="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="password" className="form-label">Password:</label>
                  <input
                    type="password"
                    className="form-control"
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
                <div className="d-grid gap-2">
                  <button type="submit" className="btn btn-primary">Login</button>
                </div>
              </form>
            </div>
            <div className="card-footer text-center">
              Don't have an account? <Link to="/signup" className="link-primary">Sign up here</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}



export default Login;