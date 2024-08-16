# Alpaca Scheduler Back-End

## Getting Started

Follow the steps [here](https://supabase.com/docs/guides/cli/local-development) to install a local supabase instance. 
This will be used for local development.

If you are unable to launch Docker Desktop when on Ubuntu 24.04, try the following:
https://forums.docker.com/t/docker-desktop-not-working-on-ubuntu-24-04/141054/2

```bash
sudo sysctl -w kernel.apparmor_restrict_unprivileged_userns=0
sudo systemctl --user restart docker-desktop
```
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