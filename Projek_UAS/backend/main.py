import socket

import bcrypt
from datetime import timedelta, datetime
from flask import Flask, request, jsonify, session
from flask_cors import CORS
from flask_login import (
    LoginManager,
    UserMixin,
    current_user,
    login_required,
    login_user,
    logout_user,
)
from flask_wtf.csrf import CSRFProtect, generate_csrf
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from sqlalchemy.ext.associationproxy import association_proxy
from marshmallow_sqlalchemy import SQLAlchemySchema
import mysql.connector


# init
app = Flask(__name__)
app.config.from_object(__name__)
app.config["SECRET_KEY"] = "spos"
app.config["SQLALCHEMY_DATABASE_URI"] = "mysql://root:@127.0.0.1/sales"
# app.config["SESSION_COOKIE_HTTPONLY"] = True
# app.config["REMEMBER_COOKIE_HTTPONLY"] = True
# app.config["SESSION_COOKIE_SAMESITE"] = "Lax"
# app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
# app.permanent_session_lifetime = timedelta(seconds=30)      

# login_manager = LoginManager()
# login_manager.init_app(app)
# login_manager.session_protection = "strong"

# CORS(
#     app, 
#     resources={r"/*":{'origins': "*"}},
#     expose_headers=["Content-Type", "X-CSRFToken"],
#     supports_credentials=True,
# )

# csrf = CSRFProtect(app)
db = SQLAlchemy(app)
ma = Marshmallow(app)





# database
# bridge table
class DetailProduct(db.Model):  
    __tablename__ = "detail_product"
    id = db.Column(db.Integer, primary_key=True)
    order_id = db.Column(db.Integer, db.ForeignKey("order.id"))
    product_id = db.Column(db.Integer, db.ForeignKey("product.id"))

    order = db.relationship("Order", back_populates="product_association")
    product = db.relationship("Product", back_populates="order_association")


# primary tables
class Sales(db.Model):
    __tablename__ = "sales"
    username = db.Column(db.String(18), primary_key=True)
    password = db.Column(db.String(18), nullable=False)

    # one to many relationship
    customers = db.relationship("Customer", backref="sales")        
    orders = db.relationship("Order", backref="sales")


class Customer(db.Model):
    __tablename__ = "customer"
    username = db.Column(db.String(18), primary_key=True)
    password = db.Column(db.String(18), nullable=False)

    sales_id = db.Column(db.String(18), db.ForeignKey("sales.username"))
    orders = db.relationship("Order", backref="customer")          # one to many relationship


class Product(db.Model):
    __tablename__ = "product"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(18), nullable=False)

    order_association = db.relationship("DetailProduct", back_populates="product")
    orders = association_proxy("order_association", "order")


class Order(db.Model):
    __tablename__ = "order"
    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.String(27), nullable=False)
    sales_id = db.Column("sales_id", db.String(18), db.ForeignKey("sales.username"))
    customer_id = db.Column("customer_id", db.String(18), db.ForeignKey("customer.username"))

    product_association = db.relationship("DetailProduct", back_populates="order")
    products = association_proxy("product_association", "product")




# schemas
class ProductSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = Product
        include_fk = True


class OrderSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = Order
        include_fk = True



# api's/endpoints
# to get all the products
@app.route("/getallproducts", methods=["GET"])
def getAllProducts():
    response_object = {"status": "success"}
    if request.method == "GET":
        products = Product.query.all()
        response_object["data"] = ProductSchema(many=True).dump(products)
        return jsonify(response_object)
    
    response_object["status"] = "fail"
    return response_object


# to get all orders
@app.route("/order/<username>", methods=["GET"])
def getOrder(username):
    response_object = {"status": "success"}
    if request.method == "GET":
        orders = Order.query.filter_by(customer_id=username).all()
        response_object["data"] = OrderSchema(many=True).dump(orders)
        return jsonify(response_object)
    
    response_object["status"] = "fail"
    return response_object



# to subscribe a customer to a sales
@app.route("/subscribe", methods=["PUT"])
def subscribe():
    response_object = {"status": "success"}
    if request.method == "PUT":
        data = request.get_json()
        sales_username = data.get("sales_username")
        customer_username = data.get("customer_username")

        # defining
        theSales = Sales.query.filter_by(username=sales_username).first()
        theCustomer = Customer.query.filter_by(username=customer_username).first()

        # to check if the query results are available
        if (theSales != None) & (theCustomer != None):
            if (theCustomer not in theSales.customers):       # to check whether or not the customer is already subscribed to the Sales 
                theSales.customers.append(theCustomer)
                db.session.commit()
                return jsonify(response_object)

    response_object = {"status": "fail"}
    return jsonify(response_object)


# to unsubscribe a customer to a sales
@app.route("/unsubscribe", methods=["PUT"])
def unsubscribe():
    response_object = {"status": "success"}
    if request.method == "PUT":
        data = request.get_json()
        sales_username = data.get("sales_username")
        customer_username = data.get("customer_username")

        # defining
        theSales = Sales.query.filter_by(username=sales_username).first()
        theCustomer = Customer.query.filter_by(username=customer_username).first()

        # to check if the query results are available
        if (theSales != None) & (theCustomer != None):
            if (theCustomer in theSales.customers):       # to check whether or not the customer is already subscribed to the Sales 
                theSales.customers.remove(theCustomer)
                db.session.commit()
                return jsonify(response_object)

    response_object = {"status": "fail"}
    return jsonify(response_object)
    

# to create an order
@app.route("/order", methods=["POST"])
def order():
    response_object = {"status": "success"}
    if request.method == "POST":
        data = request.get_json()
        sales_username = data.get("sales_username")
        customer_username = data.get("customer_username")
        products = data.get("products")

        print(f"products: {products}")

        # defining
        theSales = Sales.query.filter_by(username=sales_username).first()
        theCustomer = Customer.query.filter_by(username=customer_username).first()
        theProducts = [Product.query.filter_by(id=prd_id).first() for prd_id in products]

        # to check if the query results are available
        if (theSales != None) & (theCustomer != None) & (theProducts != None):
            # create an order
            curdate = str(datetime.now().strftime("%d-%m-%Y"))
            anOrder = Order(date=curdate)
            db.session.add(anOrder)

            # join the order with cust and sales
            theSales.orders.append(anOrder)
            theCustomer.orders.append(anOrder)

            # create the detail
            for product in theProducts:
                theDetailProduct = DetailProduct()
                product.order_association.append(theDetailProduct)
                anOrder.product_association.append(theDetailProduct)
                
            db.session.commit()
            return jsonify(response_object)

    response_object = {"status": "fail"}
    return jsonify(response_object)





    
@app.route("/")
def InitializeData():
    response_object = {"status": "success"}
    try:
        # add an sales
        salesA = Sales(username="A", password="1")
        salesB = Sales(username="B", password="2")

        # add customers
        cusA = Customer(username="cusA", password="1")
        cusB = Customer(username="cusB", password="2")

        # execute primary data
        db.session.add_all([
            salesA, salesB,
            cusA, cusB
        ])
        db.session.commit()
        print("1st added is succeed")

        # add products
        prdA = Product(name="susu")
        prdB = Product(name="bendera")

        # execute primary data
        db.session.add_all([
            prdA, prdB
        ])
        db.session.commit()
        print("2nd added is succeed")

        response_object["message"] = "all data have been added"
    except Exception as e:
        db.session.rollback()
        response_object = {"status": "fail"}
        response_object["message"] = str(e)

    return jsonify(response_object)


def create_database():
    # create the database
    conn = mysql.connector.connect(user='root', password='', host='localhost')
    cursor = conn.cursor()
    cursor.execute('CREATE DATABASE IF NOT EXISTS sales')
    conn.commit()
    conn.close()



if __name__ == "__main__":
    hostname = socket.gethostname()
    ip_address = socket.gethostbyname(hostname)
    with app.app_context():
        create_database()
        db.create_all()

    app.run(port=5000,debug=True,threaded=False,host=ip_address)