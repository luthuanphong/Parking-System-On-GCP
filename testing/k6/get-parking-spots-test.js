import http from 'k6/http';
import { environment } from './config/setting';
import { workload } from './config/workload';

const options = {
    stages: workload.normal
};

export default function() {
    
}