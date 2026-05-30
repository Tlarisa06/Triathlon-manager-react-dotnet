import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import EventTable from './components/EventTable';
import EventForm from './components/EventForm';
import EventFilter from './components/EventFilter';
import './App.css';

function App() {
    const [token, setToken] = useState(localStorage.getItem('token') || null);
    const [username, setUsername] = useState(localStorage.getItem('username') || '');
    const [events, setEvents] = useState([]);
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [loginUsername, setLoginUsername] = useState('');
    const [loginPassword, setLoginPassword] = useState('');
    const [error, setError] = useState('');

    const API_URL = 'http://localhost:8080/triathlon';

    useEffect(() => {
        if (token) {
            fetchEvents();
        }
    }, [token]);

    useEffect(() => {
        let stompClient = null;

        if (token) {
            stompClient = new Client({
                brokerURL: 'ws://localhost:8080/triathlon-websocket/websocket',
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
            });

            stompClient.onConnect = () => {
                stompClient.subscribe('/topic/events', (message) => {
                    if (message.body === 'UPDATED') {
                        fetchEvents();
                    }
                });
            };

            stompClient.activate();
        }

        return () => {
            if (stompClient) stompClient.deactivate();
        };
    }, [token]);

    const fetchEvents = async () => {
        try {
            if (!token) return;

            const response = await fetch(`${API_URL}/events`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.status === 401) {
                handleLogout();
                return;
            }

            const data = await response.json();
            setEvents(data);
        } catch (err) {
            console.error("Eroare la încărcarea evenimentelor:", err);
        }
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: loginUsername, password: loginPassword })
            });

            if (!response.ok) throw new Error('Username sau parolă incorectă!');

            const data = await response.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);

            setToken(data.token);
            setUsername(data.username);
        } catch (err) {
            setError(err.message);
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        setToken(null);
        setUsername('');
        setSelectedEvent(null);
    };

    const handleEditSelect = (event) => {
        setSelectedEvent(event);
    };

    const handleFormSubmit = () => {
        setSelectedEvent(null);
    };

    const handleFilterResults = (filteredData) => {
        setEvents(filteredData);
    };

    if (!token) {
        return (
            <div className="login-container">
                <form className="login-form" onSubmit={handleLogin}>
                    <h2>Arbitru Login - Sistem Triatlon</h2>
                    {error && <div className="error-message">{error}</div>}
                    <div className="form-group">
                        <label>Username:</label>
                        <input type="text" value={loginUsername} onChange={(e) => setLoginUsername(e.target.value)} required />
                    </div>
                    <div className="form-group">
                        <label>Parolă:</label>
                        <input type="password" value={loginPassword} onChange={(e) => setLoginPassword(e.target.value)} required />
                    </div>
                    <button type="submit">Autentificare</button>
                </form>
            </div>
        );
    }

    return (
        <div className="app-container">
            <header className="app-header">
                <h1>Panou de Control Arbitru: {username}</h1>
                <button className="logout-btn" onClick={handleLogout}>Deconectare</button>
            </header>

            <main className="app-content">
                <div className="left-panel">
                    {/* Modifică linia asta în App.js: */}
                    <EventFilter onFilter={handleFilterResults} onReset={fetchEvents} token={token} />
                    <EventForm selectedEvent={selectedEvent} onFormSubmit={handleFormSubmit} token={token} />
                </div>
                <div className="right-panel">
                    <EventTable events={events} onEdit={handleEditSelect} token={token} refreshData={fetchEvents} />
                </div>
            </main>
        </div>
    );
}

export default App;