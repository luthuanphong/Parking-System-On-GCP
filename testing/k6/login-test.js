import http from 'k6/http';
import { environment } from './config/setting.js';
import { workload } from './config/workload.js';
import { generateRandomFromRange } from './utils/RandomUtils.js'

export const options = {
    stages: workload.stress
};

export default function() {
    const url = `${environment.sandbox.URL}/users/login`;
    const random_id = generateRandomFromRange(1, 1500);
    const payload = JSON.stringify({
        "username": `user_${random_id}`,
        "password": "123456"
    });

    const params = {
        Headers: {
            'Content-Type': 'application/json'
        }
    };

    http.post(url, payload, params);
}