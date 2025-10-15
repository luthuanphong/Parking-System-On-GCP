-- users
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password_ciphertext TEXT NOT NULL, -- encrypted password blob (see KMS)
  password_salt TEXT NOT NULL,       -- salt used for hashing the password
  balance_cents BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- vehicles
CREATE TABLE vehicles (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  plate_normalized TEXT UNIQUE NOT NULL, -- e.g. uppercase no-space
  created_at TIMESTAMPTZ DEFAULT now()
);

-- parking_spots (static)
CREATE TABLE parking_spots (
  id INT PRIMARY KEY, -- 1..80
  name TEXT
);

-- reservations
CREATE TABLE reservations (
  id BIGSERIAL PRIMARY KEY,
  vehicle_id INT REFERENCES vehicles(id),
  user_id INT REFERENCES users(id),
  spot_id INT REFERENCES parking_spots(id),
  reserved_for_date DATE NOT NULL,            -- the date the spot is reserved for (next day)
  amount_cents BIGINT NOT NULL DEFAULT 1000, -- 1000 cents = $10.00
  created_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE (spot_id, reserved_for_date),        -- ensures a spot cannot be double-booked for a date
  UNIQUE (vehicle_id, reserved_for_date)      -- ensures one vehicle only has one spot per date
);
