export const workload = {
    rehersal: [
        {
            duration: '1s',
            target: 1
        }
    ],
    normal: [
        {
            duration: '3m',
            target: 750
        }
    ],
    stress: [
        {
            duration: '3m',
            target: 1500
        }
    ],
    thresholds: {
        http_req_failed: ['rate<0.1'], 
        http_req_duration: ['p(95)<60000']
    }
}