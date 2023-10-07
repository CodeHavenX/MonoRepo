Here is a simple flow chart:

```mermaid
sequenceDiagram
    Alicia->>+Reservation: getAvailableTimes<br>(appointmentType, period, staff?)
    Reservation->>+Configuration: getConfiguration<br>(appointmentType)
    Configuration-->>-Reservation: Duration
    Reservation->>+Calendar: getAvailableTimeSlots<br>(period, duration, staff?)
    Calendar-->>-Reservation: List<AvailabilityTimeSlot>
    Reservation-->>-Alicia: List<AvailabilityTimeSlot>
    Alicia->>+Reservation: book(staff?, timeSlot, user)
    Reservation->>+Calendar: book(timeSlot, staff?, user)
    Calendar-->>-Reservation: RequestConfirmation
    Reservation->>Comms: notify(timeSlot, staff, user)
    Reservation-->>-Alicia: RequestConfirmation
    Comms-->>Alicia: notify(timeSlot, staff, user)
```

Test

```mermaid
sequenceDiagram
    participant Cesar
    participant Reservation
    participant Calendar
    participant Comms
    Comms-->>Cesar: notify(timeSlot, staff, user)
    Cesar->>+Reservation: getBookedTimes<br>(period, staff)
    Reservation->>+Calendar: getBookedTimeSlots<br>(period)
    Calendar-->>-Reservation: List<TimeSlot>
    Reservation-->>-Cesar: List<TimeSlot>
    Cesar->>+Reservation: confirm(staff, timeSlot, user)
    Reservation->>+Calendar: confirm(timeSlot, staff, user)
    Calendar-->>-Reservation: BookedConfirmation
    Reservation-->>Comms: notify(timeSlot, staff, user)
    Reservation-->>-Cesar: BookedConfirmation
```