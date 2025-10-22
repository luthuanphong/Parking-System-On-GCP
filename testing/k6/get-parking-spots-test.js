import http from 'k6/http';
import { environment } from './config/setting';
import { workload } from './config/workload';
import { generateRandomFromRange } from './utils/RandomUtils.js'

const options = {
    stages: workload.normal
};

export default function() {
    const url = `${environment.sandbox.URL}/parking/spots`
    const random_id = generateRandomFromRange(1, 1500);
    const username =  `user_${random_id}`;
    const token = tokens[username];

    const params = {
        Headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    http.get(url, params);
}