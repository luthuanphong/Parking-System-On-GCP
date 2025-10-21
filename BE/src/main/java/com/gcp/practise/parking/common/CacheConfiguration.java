package com.gcp.practise.parking.common;

import java.util.Set;

public final class CacheConfiguration {

    public static final String CACHE_NAME = "parkingCache";
    public static final String PARKING_SPOTS_CACHE_NAME = "parkingSpots";
    public static final String USER_CACHE_NAME = "usersCache";
    public static final String USER_RESERVATION_REQUEST_CACHE_NAME = "userReservationRequests";
    public static final String RESERVATIONS_CACHE_NAME = "reservationsCache";
    public static final String RESERVED_USER_CACHE_NAME = "reservedUser";

    public static final String USER_REPOSITORY_CACHE = "userRepositoryCache";
    public static final String VEHICLE_REPOSITORY_BY_USER_ID_CACHE = "vehicleRepositoryByUserIDCache";
    public static final String VEHICLE_REPOSITORY_BY_ID_CACHE = "vehicleRepositoryByIDCache";
    public static final String ALL_VEHICLE = "ALL_VEHICLE";

    public static Set<String> allCacheNames = Set.of(
            CACHE_NAME,
            PARKING_SPOTS_CACHE_NAME,
            USER_CACHE_NAME,
            USER_RESERVATION_REQUEST_CACHE_NAME,
            RESERVATIONS_CACHE_NAME,
            RESERVED_USER_CACHE_NAME,
            USER_REPOSITORY_CACHE,
            VEHICLE_REPOSITORY_BY_USER_ID_CACHE,
            VEHICLE_REPOSITORY_BY_ID_CACHE,
            ALL_VEHICLE
        );
}
