# HW3 Danger Log

## 3.25

If the format of the incoming requests is invalid, we simply throw an exception and catch it directly (which means that we omit the request). Because our parser cannot parse it into a valid Action, so we cannot continue with responding it. By "invalid format" we mean the following situations:

* The root tag is neither `create` nor `transactions`. 
* If the root tag is `create`:
  * One or more child node(s) have name other than `account` and `symbol`. 
  * The child node `account` has no attribute `id` or `balance`. 
  * The child node `symbol` has no attribute `sym`. 
    * The node `symbol` has no invalid child node(s), which is not `account` or has invalid `acount`. 
* If the root tag is `transactions`: 
  * The root node does not have attribute `id`. 
  * The child node(s) have name other than `order`, `query` or `cancel`. 
  * The child node `order` has no attribute `sym`, `account` or `limit`. 
  * The child node `query` or `cancel` has no attribute `id`. 

In the future, we may need to response with an error message. 

## 3.26

If there are some invalid redundant attributes, our server will not identify them. It will work normally without sending error messages. Maybe we can fix this issue in the future. 

## 3.27

We want to make sure that if there are exceptions thrown after we parse the XML requests successfully, we can always return an error message to the client. We do this by: 

* Catch the exception. 
* Get the exception message. 
* Change this into an XML-formed string and append them to the existing response. 

## 3.29

We let all threads share the same database connection. This will limit the request resolving speed. Maybe we will enable them to share multiple database connections. 

