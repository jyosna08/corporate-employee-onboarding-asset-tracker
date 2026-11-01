# PeopleOS Postman quick start

1. In Postman choose **Import** and import `PeopleOS.postman_collection.json` and `PeopleOS.local.postman_environment.json`.
2. Select the **PeopleOS Local** environment. Set `hrEmail` and `hrPassword` to your existing HR login. Do not save or share your password in the collection.
3. Run **1. HR Login** first. It stores the returned JWT in the collection for later requests.
4. Run **2. Create employee**, then **3. List employees** and **4. Get created employee**.

The create request inserts a real employee into the employee PostgreSQL database. Use a unique `employeeNumber` and `employeeEmail` each time; duplicates return `409 Conflict`. If you only want to inspect existing data, run login and list employees and skip create.

API base URL: `http://localhost:8080`. Ensure Docker Desktop and the Compose stack are running.
