import http from 'k6/http';
import { environment } from './config/setting';
import { workload } from './config/workload';

const options = {
    stages: workload.stress
};

export default function() {
    const url = `${environment.sandbox.URL}/users/reservations`
    const random_id = generateRandomFromRange(1, 1500);
    const random_spot_id = generateRandomFromRange(1, 80);
    const username =  `user_${random_id}`;
    const token = tokens[username];

    const params = {
        Headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const payload = JSON.stringify({
        "spotId": random_spot_id
    });

    http.post(url, payload, params);
}