# Hostel-Management-API
Hostel management application created for Hostel management. It contains the entities like Hostel, Room, Tenant and Payment.

This application will have two default users like Admin & Manager. It also provides the API documentation. Kindly visit the <a href="http://localhost:8080/swagger-ui/index.html">Open API Document</a> once application is up and running.

![img.png](document/img.png)

## High level hierarchy of Hostel Management

![img.png](document/hierarchy.png)

<ol>
    <li>User can be associated with one or more hostel(s).</li>
    <li>One Hostel can have multiple rooms.</li>
    <li>One Room can have multiple tenants.</li>
    <li>One tenant can have multiple payments. Possibly monthly one payment.</li>
</ol>

## Technologies Used
<ul>
    <li>JDK - 17.x</li>
    <li>Spring boot - 3.x</li>
    <li>Maven 3.9.x</li>
    <li>MySQL - 8.x</li>
</ul>

MySQL user should be created with the credentials of root/root. If you have your own credentials, it needs to be updated in **application.yml** file.
```dtd
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hostel
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```
You should create the database with the name of hostel(it can be any name) in MySQL environment. if you want to create with different name, it needs to be updated in the above url property.

## MySQL command to create database
```dtd
create database hostel
```
verify with the below command that the hostel database has been created or not
```dtd
show databases
```