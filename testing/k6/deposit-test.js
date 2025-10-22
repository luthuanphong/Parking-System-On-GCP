import http from 'k6/http';
import { check } from 'k6';
import { environment } from './config/setting.js';
import { workload } from './config/workload.js';
import { generateRandomFromRange } from './utils/RandomUtils.js'
import { htmlReport } from './report/libs.js';
import { textSummary } from './report/k6-summary.js';
import { tokens } from './mock/mock.js';

export const options = {
    stages: workload.stress,
    thresholds: workload.thresholds
};


export default function() {
    const url = `${environment.sandbox.URL}/users/deposit`
    const random_id = generateRandomFromRange(1, 1500);
    const random_balance = generateRandomFromRange(1, 10) * 1000;
    const username =  `user_${random_id}`;
    const token = tokens[username];

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const payload = JSON.stringify({
        "email": username,
        "amountCents": random_balance
    });

    const res = http.post(url, payload, params);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}

export function handleSummary(data) {
  return {
    './output/deposit.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  }
}