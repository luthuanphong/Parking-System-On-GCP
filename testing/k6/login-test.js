import http from 'k6/http';
import { environment } from './config/setting.js';
import { workload } from './config/workload.js';

export const options = {
    stages: workload.normal
};

export default function() {
    const url = `${environment.sandbox.URL}/users/login`
    const random_id = Math.floor(Math.random() * (1500)) + 1;
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