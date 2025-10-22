import requests
import json
from typing import Optional, Dict, Any
from datetime import datetime
import copy

# Configuration
ENDPOINT_URL = "https://parking-service-api-652635654773.asia-southeast1.run.app/api/users/signup"  # Set your endpoint URL here
PAYLOAD = {
    "username": "user_",
    "password": "123456",
    "licensePlate": ""
}  # Set your payload here

LOOP_COUNT = 1500  # Set the number of times to execute the request
OUTPUT_FILE = "responses.json"  # Output JSON file name

def make_request(url: str, payload: Dict[str, Any]) -> Optional[Dict[str, Any]]:
    """
    Make a POST request to the specified endpoint.
    
    Args:
        url: The endpoint URL
        payload: The request payload
        
    Returns:
        Response data as dictionary or None if request fails
    """
    try:
        response = requests.post(url, json=payload)
        response.raise_for_status()  # Raise an exception for bad status codes
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Request failed: {e}")
        return None
    except json.JSONDecodeError as e:
        print(f"Failed to decode JSON response: {e}")
        return None

def main():
    """
    Main function to execute HTTP requests in a loop and save responses.
    """
    print(f"Starting {LOOP_COUNT} POST requests to: {ENDPOINT_URL}")
    print(f"Payload: {PAYLOAD}")
    print("-" * 50)
    
    responses = []
    
    for i in range(1, LOOP_COUNT + 1):
        print(f"Request {i}/{LOOP_COUNT}...", end=" ")
        clonedPayload = copy.deepcopy(PAYLOAD)
        clonedPayload['username'] = PAYLOAD['username'] + "{}".format(i + 1)
        clonedPayload['licensePlate'] = (i + 1)
        response_data = make_request(ENDPOINT_URL, clonedPayload)
        
        if response_data is not None:
            # Add metadata to each response
            response_entry = {
                "request_number": i,
                "timestamp": datetime.now().isoformat(),
                "response": response_data
            }
            responses.append(response_entry)
            print("✓ Success")
        else:
            print("✗ Failed")
    
    # Write all responses to JSON file
    try:
        with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
            json.dump(responses, f, indent=2, ensure_ascii=False)
        print("-" * 50)
        print(f"✓ Successfully wrote {len(responses)} responses to {OUTPUT_FILE}")
    except Exception as e:
        print(f"✗ Failed to write to file: {e}")

if __name__ == "__main__":
    main()