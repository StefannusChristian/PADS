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
from sqlalchemy import text


# init
app = Flask(__name__)
app.config.from_object(__name__)
app.config["SECRET_KEY"] = "spos"
app.config["SQLALCHEMY_DATABASE_URI"] = "mysql://root:@127.0.0.1/sales"
app.config["co-password"] = "jasonganteng"
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
# bridge tables
class DetailOrder(db.Model):
    __tablename__ = "detail_order"
    id = db.Column(db.Integer, primary_key=True)
    order_id = db.Column(db.Integer, db.ForeignKey("order.id"), nullable=False)
    product_id = db.Column(db.Integer, db.ForeignKey("product.id"), nullable=False)
    qty = db.Column(db.Integer, nullable=False)

    order = db.relationship("Order", back_populates="product_association")
    product = db.relationship("Product", back_populates="order_association")


class DetailCart(db.Model):
    __tablename__ = "detail_cart"
    id = db.Column(db.Integer, primary_key=True)
    cart_id = db.Column(db.Integer, db.ForeignKey("cart.id"), nullable=False)
    product_id = db.Column(db.Integer, db.ForeignKey("product.id"), nullable=False)
    qty = db.Column(db.Integer, nullable=False)
    is_available = db.Column(db.Boolean, nullable=False)

    cart = db.relationship("Cart", back_populates="product_association")
    product = db.relationship("Product", back_populates="cart_association")





# primary tables
class Sales(db.Model):
    __tablename__ = "sales"
    username = db.Column(db.String(18), primary_key=True)
    password = db.Column(db.String(18), nullable=False)
    name = db.Column(db.String(27), nullable=False)
    email = db.Column(db.String(27), nullable=False)
    verified = db.Column(db.Boolean, nullable=False)

    # one to many relationship
    customers = db.relationship("Customer", backref="sales")        
    orders = db.relationship("Order", backref="sales")

    # one to one relationship
    cart = db.relationship("Cart", backref="sales", uselist=False)


class Customer(db.Model):
    __tablename__ = "customer"
    username = db.Column(db.String(18), primary_key=True)
    address = db.Column(db.String(99), nullable=False)

    sales_id = db.Column(db.String(18), db.ForeignKey("sales.username"))
    orders = db.relationship("Order", backref="customer")          # one to many relationship


class Product(db.Model):
    __tablename__ = "product"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(18), nullable=False)
    price = db.Column(db.Integer, nullable=False)
    available_qty = db.Column(db.Integer, nullable=False)
    ordered_qty = db.Column(db.Integer, nullable=False)
    total_qty = db.Column(db.Integer, nullable=False)
    is_promo = db.Column(db.Boolean, nullable=False)
    img_link = db.Column(db.String(99))

    order_association = db.relationship("DetailOrder", back_populates="product")
    orders = association_proxy("order_association", "order")                # many to many relationship

    cart_association = db.relationship("DetailCart", back_populates="product")
    carts = association_proxy("cart_association", "cart")                   # many to many relationship

    # warehouse_association = db.relationship("DetailWarehouse", back_populates="product")
    # warehouses = association_proxy("warehouse_association", "warehouse")    # many to many relationship

    # one to many relationship
    warehouse_id = db.Column(db.Integer, db.ForeignKey("warehouse.id"), nullable=False)


class Order(db.Model):
    __tablename__ = "order"
    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.String(27), nullable=False)
    status = db.Column(db.String(9), nullable=False)
    total_price = db.Column(db.Integer, nullable=False)
    sales_id = db.Column("sales_id", db.String(18), db.ForeignKey("sales.username"), nullable=False)
    customer_id = db.Column("customer_id", db.String(18), db.ForeignKey("customer.username"), nullable=False)

    product_association = db.relationship("DetailOrder", back_populates="order")
    products = association_proxy("product_association", "product")


class Cart(db.Model):
    __tablename__ = "cart"
    id = db.Column(db.Integer, primary_key=True)
    sales_id = db.Column("sales_id", db.String(18), db.ForeignKey("sales.username"), nullable=False)
    qty = db.Column(db.Integer, nullable=False)

    product_association = db.relationship("DetailCart", back_populates="cart")
    products = association_proxy("product_association", "product")


class Warehouse(db.Model):
    __tablename__ = "warehouse"
    id = db.Column(db.Integer, primary_key=True)
    ordered_qty = db.Column(db.Integer, nullable=False)
    available_qty = db.Column(db.Integer, nullable=False)
    total_qty = db.Column(db.Integer, nullable=False)

    # many to many relationship
    # product_association = db.relationship("DetailWarehouse", back_populates="warehouse")
    # products = association_proxy("product_association", "product")

    # one to many relationship
    products = db.relationship("Product", backref="warehouse")




# schemas
class CustomerSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = Customer
        include_fk = True


class SalesSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = Sales
        include_fk = True


class ProductSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = Product
        include_fk = True


class OrderSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = Order
        include_fk = True


class DetailCartSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = DetailCart
        include_fk = True




# api's/endpoints
# to get all products
@app.route("/getallproducts", methods=["GET"])
def getAllProducts():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "GET":
            products = Product.query.all()
            response_object["data"] = ProductSchema(many=True).dump(products)
            response_object["status"], response_object["message"] = "success", "-"
            return jsonify(response_object)
        
        return response_object
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)


# to gett all promo products
@app.route("/getallpromos", methods=["GET"])
def getAllPromos():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "GET":
            promos = Product.query.filter_by(is_promo=True).all()
            response_object["data"] = ProductSchema(many=True).dump(promos)
            response_object["status"], response_object["message"] = "success", "-"
            return jsonify(response_object)
        
        return response_object
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)


# to get all sales orders
@app.route("/getorders/<username>", methods=["GET"])
def getOrders(username):
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "GET":
            theSales = Sales.query.filter_by(username=username).first()
            # to check if the query results are available
            if (theSales != None):
                orders = Order.query.filter_by(sales_id=username).all()
                response_object["data"] = OrderSchema(many=True).dump(orders)
                response_object["status"], response_object["message"] = "success", "-"
                return jsonify(response_object)
            else:
                response_object["message"] = "the sales is missing"

        return response_object
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to get all sales customers
@app.route("/getcustomers/<username>", methods=["GET"])
def getCustomers(username):
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "GET":
            theSales = Sales.query.filter_by(username=username).first()
            # to check if the query results are available
            if (theSales != None):
                customers = Customer.query.filter_by(sales_id=username).all()
                response_object["data"] = CustomerSchema(many=True).dump(customers)
                response_object["status"], response_object["message"] = "success", "-"
                return jsonify(response_object)
            else:
                response_object["message"] = "the sales is missing"

        return response_object
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to get sales data
@app.route("/getsales/<username>", methods=["GET"])
def getSales(username):
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "GET":
            theSales = Sales.query.filter_by(username=username).first()
            # to check if the query results are available
            if (theSales != None):
                response_object["data"] = SalesSchema().dump(theSales)
                response_object["status"], response_object["message"] = "success", "-"
                return jsonify(response_object)
            else:
                response_object["message"] = "the sales is missing"

        return response_object
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to get all sales detail carts
@app.route("/getdetailcarts/<username>", methods=["GET"])
def getDetailCarts(username):
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "GET":
            theSales = Sales.query.filter_by(username=username).first()
            theCart = Cart.query.filter_by(sales_id=username).first()
            # to check if the query results are available
            if (theSales != None) & (theCart != None):
                # to get all detail carts
                theDetailCarts = DetailCart.query.filter_by(cart_id=theCart.id).all()
                response_object["data"] = DetailCartSchema(many=True).dump(theDetailCarts)
                response_object["status"], response_object["message"] = "success", "-"
                return jsonify(response_object)
            else:
                response_object["message"] = "either sales or cart is missing"

        return response_object
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to verify/unverify a sales
@app.route("/salesverifyunverify", methods=["PUT"])
def salesVerifyUnverify():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "PUT":
            data = request.get_json()
            sales_username = data.get("sales_username")
            co_password = data.get("co-password")
            order = data.get("order")

            # defining
            theSales = Sales.query.filter_by(username=sales_username).first()

            # to check if the query results are available
            if (theSales != None):     
                if (co_password == app.config["co-password"]):
                    if (theSales.verified):
                        if (order == "verify"):
                            response_object["message"] = "the sales is verified already"
                        elif (order == "unverify"):
                            theSales.verified = False
                            db.session.commit()
                            response_object["status"], response_object["message"] = "success", "-"
                            return jsonify(response_object)
                        else:
                            response_object["message"] = "invalid order"
                    else:
                        if (order == "verify"):
                            theSales.verified = True
                            db.session.commit()
                            response_object["status"], response_object["message"] = "success", "-"
                            return jsonify(response_object)
                        elif (order == "unverify"):
                            response_object["message"] = "the sales is unverified already"
                        else:
                            response_object["message"] = "invalid order"
                else:
                    response_object["message"] = "you are not authorized to make this call" 
            else:
                response_object["message"] = "sales is missing"
                
        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)


# to subscribe a customer to a sales
@app.route("/subscribe", methods=["PUT"])
def subscribe():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "PUT":
            data = request.get_json()
            sales_username = data.get("sales_username")
            customer_username = data.get("customer_username")

            # defining
            theSales = Sales.query.filter_by(username=sales_username).first()
            theCustomer = Customer.query.filter_by(username=customer_username).first()

            # to check if the query results are available
            if (theSales != None) & (theCustomer != None):
                # to check whether or not the customer is already subscribed the sales
                if (theCustomer not in theSales.customers):        
                    if (theCustomer.sales_id == None):
                        theSales.customers.append(theCustomer)
                        db.session.commit()
                        response_object["status"], response_object["message"] = "success", "-"
                        return jsonify(response_object)
                    else:
                       response_object["message"] = "the customer is already subscribed another sales" 
                else:
                    response_object["message"] = "the customer is already subscribed the sales"
            else:
                response_object["message"] = "either the sales, or the customer is missing"
                
        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)


# to unsubscribe a customer to a sales
@app.route("/unsubscribe", methods=["PUT"])
def unsubscribe():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "PUT":
            data = request.get_json()
            sales_username = data.get("sales_username")
            customer_username = data.get("customer_username")

            # defining
            theSales = Sales.query.filter_by(username=sales_username).first()
            theCustomer = Customer.query.filter_by(username=customer_username).first()

            # to check if the query results are available
            if (theSales != None) & (theCustomer != None):
                # to check whether or not the customer is already subscribed the Sales
                if (theCustomer in theSales.customers):        
                    theSales.customers.remove(theCustomer)
                    db.session.commit()
                    response_object["status"], response_object["message"] = "success", "-"
                    return jsonify(response_object)
                else:
                    response_object["message"] = "the customer is already unsubscribed the sales"
            else:
                response_object["message"] = "either the sales, or the customer is missing"

        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to confirm an order
@app.route("/confirmorder", methods=["PUT"])
def confirmOrder():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "PUT":
            data = request.get_json()
            order_id = data.get("order_id")
            sales_id = data.get("sales_username")

            # defining
            theOrder = Order.query.filter_by(id=order_id, sales_id=sales_id).first()

            # to check if the query result is available
            if theOrder != None:
                # to check whether or not the order is active
                if (theOrder.status == "active"):        
                    theOrder.status = "sent"        # update order status

                    db.session.commit()
                    response_object["status"], response_object["message"] = "success", "-"
                    return jsonify(response_object)
                else:
                    response_object["message"] = f"the order is already {theOrder.status}"
            else:
                response_object["message"] = "the order is missing"

        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to confirm an order
@app.route("/cancelorder", methods=["PUT"])
def cancelOrder():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "PUT":
            data = request.get_json()
            sales_id = data.get("sales_username")
            order_id = data.get("order_id")

            # defining
            theOrder = Order.query.filter_by(id=order_id, sales_id=sales_id).first()

            # to check if the query result is available
            if theOrder != None:
                # to check whether or not the order is active
                if (theOrder.status == "active"):        
                    # update product stock
                    for product in theOrder.products:
                        product.available_qty += DetailOrder.query.filter_by(order_id=order_id, product_id=product.id).first().available_qty

                    # update order status
                    theOrder.status = "canceled"        

                    db.session.commit()
                    response_object["status"], response_object["message"] = "success", "-"
                    return jsonify(response_object)
                else:
                    response_object["message"] = f"the order is already {theOrder.status}"
            else:
                response_object["message"] = "the order is missing"

        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to add an order
@app.route("/addorder", methods=["POST"])
def addOrder():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "POST":
            data = request.get_json()
            sales_username = data.get("sales_username")
            customer_username = data.get("customer_username")
            cartproductids = data.get("cartproductids")

            # defining
            theSales = Sales.query.filter_by(username=sales_username).first()
            theCustomer = Customer.query.filter_by(username=customer_username).first()
            theCart = Cart.query.filter_by(sales_id=sales_username).first()
            theCartProducts = [
                DetailCart.query.filter_by(
                    id=cartproductid, cart_id=theCart.id
                ).first() for cartproductid in cartproductids
            ]

            print(len(theCartProducts))

            # to check if the query results are available
            if (theSales != None) & (theCustomer != None) & (None not in theCartProducts):
                # to check if the customer inputted is already subscribed the sales
                if (theCustomer in theSales.customers):
                    # create an order
                    curdate = str(datetime.now().strftime("%d-%m-%Y"))
                    anOrder = Order(date=curdate, status="active", sales_id=theSales.username, customer_id=theCustomer.username, total_price=0)
                    db.session.add(anOrder)

                    newTotalPrice = 0

                    # create the detail
                    for cartProduct in theCartProducts:
                        theProduct = Product.query.filter_by(id=cartProduct.product_id).first()
                        theDetailOrder = DetailOrder(qty=cartProduct.qty, order_id=anOrder.id, product_id=theProduct.id)
                        db.session.add(theDetailOrder)

                        # update product stock
                        theProduct.available_qty -= cartProduct.qty if (cartProduct.qty <= theProduct.available_qty) else theProduct.available_qty

                        # remove the cart product
                        db.session.delete(cartProduct)

                        # update total price
                        newTotalPrice += (theProduct.price*cartProduct.qty)

                    # update the total price in order
                    anOrder.total_price = newTotalPrice

                    db.session.commit()
                    response_object["status"], response_object["message"] = "success", "-"
                    return jsonify(response_object)
                else:
                    response_object["message"] = "the customer is not subscribed the sales yet"
            else:
                response_object["message"] = "whether sales, customer, or list of cartproducts is missing"

        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to add certain number of product to the cart 
@app.route("/addcartproduct", methods=["POST"])
def addCartProduct():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "POST":
            data = request.get_json()
            sales_id = data.get("sales_username")
            product_id = data.get("product_id")
            qty = data.get("qty")

            # defining
            theCart = Cart.query.filter_by(sales_id=sales_id).first()
            theProduct = Product.query.filter_by(id=product_id).first()

            # to check if the query results are available
            if (theCart != None) & (theProduct != None):
                # to check if the product is already in the cart
                if (theDetailCart := DetailCart.query.filter_by(cart_id=theCart.id, product_id=theProduct.id).first()) != None:
                    theDetailCart.qty += qty if (theProduct.available_qty-theDetailCart.qty) >= qty else theProduct.available_qty-theDetailCart.qty
                else:
                    # to make sure if the product stock is 0, then it can not be added to the cart
                    if (theProduct.available_qty > 0):
                        # add the product to the cart
                        theDetailCart = DetailCart(qty=qty if theProduct.available_qty >= qty else theProduct.available_qty, is_available=True, cart_id=theCart.id, product_id=theProduct.id)
                        db.session.add(theDetailCart)
                    else:
                        response_object["message"] = "the product stock is not available"
                        return jsonify(response_object)
                        
                db.session.commit()
                response_object["status"], response_object["message"] = "success", "-"
                return jsonify(response_object)
            else:
                response_object["message"] = "either cart or product is missing"

        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to reduce certain number of product from the cart 
@app.route("/reducecartproduct", methods=["POST"])
def reduceCartProduct():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "POST":
            data = request.get_json()
            sales_id = data.get("sales_username")
            product_id = data.get("product_id")
            qty = data.get("qty")

            # defining
            theCart = Cart.query.filter_by(sales_id=sales_id).first()
            theProduct = Product.query.filter_by(id=product_id).first()

            # to check if the query results are available
            if (theCart != None) & (theProduct != None):
                # to check if the product is already in the cart
                if (theDetailCart := DetailCart.query.filter_by(cart_id=theCart.id, product_id=theProduct.id).first()) != None:
                    if theDetailCart.qty > qty:
                        theDetailCart.qty -= qty if theDetailCart.qty >= qty else theDetailCart.qty
                    else:
                        db.session.delete(theDetailCart)
                    
                    db.session.commit()
                    response_object["status"], response_object["message"] = "success", "-"
                else:
                    response_object["message"] = "the product has already been removed from the cart"
                        
                return jsonify(response_object)
            else:
                response_object["message"] = "either cart or product is missing"

        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)
    

# to remove certain product from the cart 
@app.route("/removecartproduct", methods=["POST"])
def removeCartProduct():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        if request.method == "POST":
            data = request.get_json()
            sales_id = data.get("sales_username")
            product_id = data.get("product_id")

            # defining
            theCart = Cart.query.filter_by(sales_id=sales_id).first()
            theProduct = Product.query.filter_by(id=product_id).first()

            # to check if the query results are available
            if (theCart != None) & (theProduct != None):
                # to check if the product is already in the cart
                if (theDetailCart := DetailCart.query.filter_by(cart_id=theCart.id, product_id=theProduct.id).first()) != None:
                    # remove the product from the cart
                    db.session.delete(theDetailCart)

                    db.session.commit()
                    response_object["status"], response_object["message"] = "success", "-"
                else:
                    response_object["message"] = "product is not in the cart"
                        
                return jsonify(response_object)
            else:
                response_object["message"] = "either cart or product is missing"

        return jsonify(response_object)
    except Exception as e:
         response_object["message"] = str(e)
         return jsonify(response_object)





    
@app.route("/")
def InitializeData():
    response_object = {
        "status": "fail",
        "message": "error occured"
    }

    try:
        # add an sales
        salesA = Sales(username="salesA", password="sA", name="Jason", email="jpiay40@students.calvin.ac.id", verified=True)
        salesB = Sales(username="salesB", password="sB", name="Caleb", email="cjsonnnnn@gmail.com", verified=False)

        # add their carts. Notice that, since it is one to one relationship (uselist=False), so we use a normal assign method, instead of append
        cartA = Cart(qty=0); salesA.cart = cartA
        cartB = Cart(qty=0); salesB.cart = cartB

        # add customers
        cusA = Customer(
            username="cusA", 
            address="SHINJUKU EASTSIDE SQUARE 6-27-30 Shinjuku, Shinjuku-ku, Tokyo 160-8430, Japan"
        )
        cusB = Customer(
            username="cusB", 
            address="Residence No. 55 Central Luxury Mansion"
        )

        # execute sales and customer data
        db.session.add_all([
            salesA, salesB,
            cusA, cusB
        ])
        db.session.commit()


        # define a warehouse
        theWarehouse = Warehouse(
            ordered_qty = 0,
            available_qty = 0,
            total_qty = 0
        )
        
        # execute warehouse
        db.session.add(theWarehouse)
        db.session.commit()


        # add products
        prdA = Product(
            name="milk",
            price=13000,
            available_qty=36,
            ordered_qty=0,
            total_qty=36,
            is_promo=True,
            img_link = "https://tinyurl.com/2wnaftrs",
            warehouse_id = theWarehouse.id
        )
        prdB = Product(
            name="eyeglasses",
            price=33000,
            available_qty=3,
            ordered_qty=0,
            total_qty=3,
            is_promo=False,
            img_link = "https://tinyurl.com/3hdeezjk",
            warehouse_id = theWarehouse.id
        )
        prdC = Product(
            name="t-shirt",
            price=73000,
            available_qty=6,
            ordered_qty=0,
            total_qty=6,
            is_promo=False,
            img_link = "https://tinyurl.com/bdea4eb5",
            warehouse_id = theWarehouse.id
        )
        prdD = Product(
            name="christmas hat",
            price=35000,
            available_qty=312,
            ordered_qty=0,
            total_qty=312,
            is_promo=False,
            img_link = "https://tinyurl.com/5by4dd2p",
            warehouse_id = theWarehouse.id
        )

        # execute product data
        db.session.add_all([
            prdA, prdB, prdC, prdD
        ])
        db.session.commit()

        response_object["status"], response_object["message"]  = "success", "all data has been successfully added"
    except Exception as e:
        db.session.rollback()
        response_object["status"], response_object["message"] = "fail", str(e)

    return jsonify(response_object)


# to automatically create a new database, in case it does not exist
def create_database():
    # create the database
    conn = mysql.connector.connect(user='root', password='', host='localhost')
    cursor = conn.cursor()
    cursor.execute('CREATE DATABASE IF NOT EXISTS sales')
    conn.commit()
    conn.close()


# define some triggers
triggers = [
    # to update qty in cart based on number of detail_carts
    """
    CREATE TRIGGER update_cart_qty_on_insert_detail_cart_trigger 
    AFTER INSERT
    ON detail_cart
    FOR EACH ROW 
    BEGIN
        DECLARE cart_qty_insert INTEGER;
        SELECT COUNT(*) INTO cart_qty_insert FROM detail_cart WHERE cart_id = NEW.cart_id;
        UPDATE cart SET qty = cart_qty_insert WHERE id = NEW.cart_id;
    END
    """,
    # to update qty in cart based on number of detail_carts
    """
    CREATE TRIGGER update_cart_qty_on_delete_detail_cart_trigger 
    AFTER DELETE
    ON detail_cart
    FOR EACH ROW 
    BEGIN  
        DECLARE cart_qty_delete INTEGER;
        SELECT COUNT(*) INTO cart_qty_delete FROM detail_cart WHERE cart_id = OLD.cart_id;
        UPDATE cart SET qty = cart_qty_delete WHERE id = OLD.cart_id;
    END
    """,
    # to update qty in detail_cart based on product qty
    """
    CREATE TRIGGER update_detail_cart_qty_on_update_product_trigger
    AFTER UPDATE 
    ON product
    FOR EACH ROW
    BEGIN
        IF NEW.available_qty != OLD.available_qty THEN
            UPDATE detail_cart cd
            SET cd.qty = CASE
                            WHEN cd.qty > NEW.available_qty THEN NEW.available_qty
                            ELSE cd.qty
                        END
            WHERE cd.product_id = NEW.id;
        END IF;
    END;
    """,
    # to update is_available in detail_cart based on qty
    """
    CREATE TRIGGER update_detail_cart_is_available_on_update_detail_cart_trigger 
    BEFORE UPDATE
    ON detail_cart
    FOR EACH ROW 
    BEGIN
        IF NEW.qty > 0 THEN
            SET NEW.is_available = 1;
        ELSE
            SET NEW.is_available = 0;
        END IF;
    END;
    """,
    # to update ordered_qty in product everytime insertion on detail_order
    """
    CREATE TRIGGER update_product_ordered_qty_on_insert_detail_order_trigger
    AFTER INSERT 
    ON detail_order
    FOR EACH ROW
    BEGIN
        UPDATE product p
        SET p.ordered_qty = (
            SELECT SUM(qty) 
            FROM detail_order 
            WHERE order_id IN (
                SELECT id 
                FROM `order` 
                WHERE status IN ('active', 'sent')
            ) AND product_id = NEW.product_id
        )
        WHERE p.id = NEW.product_id;
    END;
    """,
    # to update ordered_qty in product everytime updation on order
    """
    CREATE TRIGGER update_product_ordered_qty_on_update_order_trigger
    AFTER UPDATE 
    ON `order`
    FOR EACH ROW
    BEGIN
        UPDATE product p
        SET p.ordered_qty = (
            SELECT SUM(qty)
            FROM detail_order
            WHERE order_id IN (
                SELECT id 
                FROM `order` 
                WHERE status IN ('active', 'sent')
            ) AND product_id = p.id
        )
        WHERE p.id = (SELECT id FROM detail_order WHERE order_id=NEW.id AND product_id = p.id);
    END;
    """,
    # to update total_qty in product everytime updation on product
    """
    CREATE TRIGGER update_product_total_qty_on_update_product_trigger
    BEFORE UPDATE 
    ON product
    FOR EACH ROW
    SET NEW.total_qty = NEW.ordered_qty + NEW.available_qty;
    """,
    # to update available_qty in warehouse based on product qty
    """
    CREATE TRIGGER update_warehouse_available_qty_on_update_product_trigger
    AFTER UPDATE 
    ON product
    FOR EACH ROW
    BEGIN
        UPDATE warehouse w
        SET w.available_qty = (SELECT SUM(available_qty) FROM `product`);
    END;
    """,
    # to update ordered_qty in warehouse based on product qty
    """
    CREATE TRIGGER update_warehouse_ordered_qty_on_update_product_trigger
    AFTER UPDATE 
    ON product
    FOR EACH ROW
    BEGIN
        UPDATE warehouse w
        SET w.ordered_qty = (SELECT SUM(ordered_qty) FROM `product`);
    END;
    """,
    # to update total_qty in warehouse based on product qty
    """
    CREATE TRIGGER update_warehouse_total_qty_on_update_product_trigger
    AFTER UPDATE 
    ON product
    FOR EACH ROW
    BEGIN
        UPDATE warehouse w
        SET w.total_qty = (SELECT SUM(total_qty) FROM `product`);
    END;
    """,
]


# to put the triggers in to database
def create_triggers():
    with db.engine.connect() as con:
        try:
            for trigger in triggers:
                con.execute(text(trigger))
        except Exception as e:
            # print(str(e))
            pass


if __name__ == "__main__":
    hostname = socket.gethostname()
    ip_address = socket.gethostbyname(hostname)
    
    with app.app_context():
        create_database()
        db.create_all()
        create_triggers()
        
    # app.run(port=5000,debug=True,threaded=False,host=ip_address)
    app.run()









# quick information and tips:
# - for the sake of implementing a best practice, we do not assign a value to the attributes that are either PK or FK manually... but I just realized, that it means I can't define the column as nullable=False. So... yeah, nvm
# - backref will create an extra column to the target in database
# - the difference between PUT and POST is based on whether or not the number of rows is changing

# task: cors! 1





# if __name__ == "__main__":
#     hostname = socket.gethostname()
#     ip_address = socket.gethostbyname(hostname)
#     with app.app_context():
#         create_database()
#         db.create_all()

#     app.run(port=5000,debug=True,threaded=False,host=ip_address)