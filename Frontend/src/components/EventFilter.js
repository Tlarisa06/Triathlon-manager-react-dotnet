import React, { useState } from 'react';
import axios from 'axios';

const EventFilter = ({ onFilter, onReset, token }) => { // <--- Am adăugat token aici
    const [filterValue, setFilterValue] = useState('');
    const [filterType, setFilterType] = useState('refereeId');

    const handleFilter = () => {
        if (!filterValue) {
            alert("Introdu o valoare!");
            return;
        }

        axios.get(`http://localhost:8080/triathlon/events?${filterType}=${filterValue}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(res => onFilter(res.data))
            .catch(err => {
                console.error(err);
                alert("Eroare la filtrare sau sesiune neautorizată!");
            });
    };

    const handleResetClick = () => {
        setFilterValue('');
        onReset();
    };

    return (
        <div style={{ padding: '15px', margin: '20px', backgroundColor: '#f1f2f6', borderRadius: '10px', display: 'flex', gap: '10px', alignItems: 'center' }}>
            <strong>🔍 Caută după:</strong>
            <select value={filterType} onChange={(e) => setFilterType(e.target.value)} style={{ padding: '5px' }}>
                <option value="refereeId">Referee ID</option>
                <option value="participantId">Participant ID</option>
                <option value="points">Puncte</option>
            </select>
            <input
                type="number"
                value={filterValue}
                onChange={(e) => setFilterValue(e.target.value)}
                placeholder="Valoare..."
                style={{ padding: '5px' }}
            />
            <button onClick={handleFilter} style={{ backgroundColor: '#3498db', color: 'white', border: 'none', padding: '7px 15px', borderRadius: '5px', cursor: 'pointer' }}>Caută</button>
            <button onClick={handleResetClick} style={{ padding: '7px 15px', borderRadius: '5px', cursor: 'pointer' }}>Reset</button>
        </div>
    );
};

export default EventFilter;