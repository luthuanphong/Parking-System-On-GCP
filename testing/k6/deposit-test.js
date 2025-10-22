import http from 'k6/http';
import { environment } from './config/setting';
import { workload } from './config/workload';
import { generateRandomFromRange } from './utils/RandomUtils.js'

const options = {
    stages: workload.stress
};

export default function() {
    const url = `${environment.sandbox.URL}/users/deposit`
    const random_id = generateRandomFromRange(1, 1500);
    const random_balance = generateRandomFromRange(1, 10) * 1000;
    const username =  `user_${random_id}`;
    const token = tokens[username];

    const params = {
        Headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const payload = JSON.stringify({
        "email": username,
        "amountCents": random_balance
    });

    http.post(url, payload, params);
}