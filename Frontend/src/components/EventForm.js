import React, { useState, useEffect } from 'react';
import axios from 'axios';

const EventForm = ({ selectedEvent, onFormSubmit, token }) => {
    const [eventData, setEventData] = useState({ idRef: '', idPart: '', points: '' });

    useEffect(() => {
        if (selectedEvent) {
            setEventData(selectedEvent);
        } else {
            setEventData({ idRef: '', idPart: '', points: '' });
        }
    }, [selectedEvent]);

    const handleSubmit = (e) => {
        e.preventDefault();

        const refId = parseInt(eventData.idRef);
        const partId = parseInt(eventData.idPart);
        const pts = parseInt(eventData.points);

        if (isNaN(refId) || isNaN(partId) || isNaN(pts)) {
            alert("Te rugăm să completezi toate câmpurile cu numere.");
            return;
        }

        if (refId < 0 || partId < 0 || pts < 0) {
            alert("Valorile nu pot fi negative!");
            return;
        }

        const payload = {
            idRef: refId,
            idPart: partId,
            points: pts
        };

        const axiosConfig = {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        };

        if (selectedEvent) {
            axios.put(`http://localhost:8080/triathlon/events/${selectedEvent.id}`, { ...payload, id: selectedEvent.id }, axiosConfig)
                .then(() => {
                    alert("Modificare realizată!");
                    onFormSubmit();
                })
                .catch(err => {
                    const msg = err.response?.data || "Eroare la modificare.";
                    alert("Server Error: " + msg);
                });
        } else {
            axios.post('http://localhost:8080/triathlon/events', payload, axiosConfig)
                .then(() => {
                    alert("Adăugat cu succes!");
                    onFormSubmit();
                })
                .catch(err => {
                    const msg = err.response?.data || "Eroare la adăugare.";
                    alert("Server Error: " + msg);
                });
        }

        setEventData({ idRef: '', idPart: '', points: '' });
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', margin: '20px', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
            <h3>{selectedEvent ? "Modifică Proba" : "Adaugă Probă Nouă"}</h3>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexWrap: 'wrap', gap: '15px', justifyContent: 'center', alignItems: 'flex-end' }}>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <label style={{ fontSize: '12px', marginBottom: '5px' }}>Referee ID</label>
                    <input
                        type="number"
                        placeholder="ID"
                        value={eventData.idRef}
                        onChange={(e) => setEventData({...eventData, idRef: e.target.value})}
                        required
                    />
                </div>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <label style={{ fontSize: '12px', marginBottom: '5px' }}>Participant ID</label>
                    <input
                        type="number"
                        placeholder="ID"
                        value={eventData.idPart}
                        onChange={(e) => setEventData({...eventData, idPart: e.target.value})}
                        required
                    />
                </div>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <label style={{ fontSize: '12px', marginBottom: '5px' }}>Puncte</label>
                    <input
                        type="number"
                        placeholder="Valoare"
                        value={eventData.points}
                        onChange={(e) => setEventData({...eventData, points: e.target.value})}
                        required
                    />
                </div>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <button type="submit" style={{
                        backgroundColor: selectedEvent ? '#ffc107' : '#28a745',
                        color: 'white',
                        border: 'none',
                        padding: '8px 20px',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        fontWeight: 'bold'
                    }}>
                        {selectedEvent ? "Salvează" : "Adaugă"}
                    </button>
                    {selectedEvent && (
                        <button type="button" onClick={() => onFormSubmit()} style={{ padding: '8px 20px', borderRadius: '4px', cursor: 'pointer' }}>
                            Anulează
                        </button>
                    )}
                </div>
            </form>
        </div>
    );
};

export default EventForm;