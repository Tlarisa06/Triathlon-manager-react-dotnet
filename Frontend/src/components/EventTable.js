import React from 'react';
import axios from 'axios';

const EventTable = ({ events, onEdit, refreshData, token }) => {
    const handleDelete = (id) => {
        if (window.confirm("Ștergi proba?")) {
            axios.delete(`http://localhost:8080/triathlon/events/${id}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
                .then(() => refreshData())
                .catch((err) => {
                    if (err.response?.status === 401) {
                        alert("Sesiune neautorizată sau expirată!");
                    } else {
                        alert("Eroare ștergere");
                    }
                });
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', boxShadow: '0 2px 10px rgba(0,0,0,0.1)' }}>
                <thead>
                <tr style={{ backgroundColor: '#3498db', color: 'white' }}>
                    <th style={{ padding: '12px' }}>ID</th>
                    <th style={{ padding: '12px' }}>Referee ID</th>
                    <th style={{ padding: '12px' }}>Participant ID</th>
                    <th style={{ padding: '12px' }}>Puncte</th>
                    <th style={{ padding: '12px' }}>Acțiuni</th>
                </tr>
                </thead>
                <tbody>
                {events.map((ev, index) => (
                    <tr key={ev.id} style={{ backgroundColor: index % 2 === 0 ? '#fff' : '#f9f9f9', textAlign: 'center' }}>
                        <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{ev.id}</td>
                        <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{ev.idRef}</td>
                        <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{ev.idPart}</td>
                        <td style={{ padding: '10px', borderBottom: '1px solid #ddd', color: '#27ae60', fontWeight: 'bold' }}>{ev.points}</td>
                        <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>
                            <button onClick={() => onEdit(ev)} style={{ backgroundColor: '#f1c40f', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer', marginRight: '5px' }}>✏️</button>
                            <button onClick={() => handleDelete(ev.id)} style={{ backgroundColor: '#e74c3c', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer' }}>🗑️</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default EventTable;