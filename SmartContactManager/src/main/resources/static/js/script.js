const togglesidebar = () => {
  //used Jquery to target tag
  if ($(".sidebar").is(":visible")) {
    //if toggle bar visible then hide it
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%");
  } else {
    //if toggle bar hiddne then show it
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%");
  }
};

//validation code

function validateForm() {
  var name = document.getElementById("firstName").value;
  var phone = document.getElementById("phone").value;

  if (name == "") {
    document.getElementById("name-field-msg").innerHTML =
      "please fill username";
    return false;
  }

  if (phone == "") {
    document.getElementById("phone-field-msg").innerHTML =
      "please enter Mobile number";
    return false;
  }

  return true;
}


//first request to server for create order
var paymentStart = () => {
  var amount = document.getElementById("payment_field").value;
  console.log(amount);
  if (amount == "" || amount == null) {
    alert("amount is required");
    return;
  }

  // JQuery Agax is used to send req to server for create order
  $.ajax({
    url: "/user/create_order",
    data: JSON.stringify({ amount: amount }),
    contentType: "application/json",
    type: "POST",
    success: function (json) {
      // received response in String and convert it into JSON

      let response = JSON.parse(json);

      let options = {
        key: "rzp_test_7rRM1YcnaKEjKh", // Enter the Key ID generated from the Dashboard
        amount: response.amount,
        currency: response.currency,
        name: "Smart Contact Manager",
        description: "Donation",
        order_id: response.id,

        prefill: {
          name: "Abhijeet Fartare",
          email: "abhi@gmail.com",
          contact: "9999999999",
        },
        notes: {
          address: "Razorpay Corporate Office",
        },
        theme: {
          color: "#3399cc",
        },
        //after completion payment
        handler: function (response) {
          var data = JSON.stringify(response);
          console.log(data);
          window.location.href = "http://localhost:8080/user/index";
        },
      };

      var rzp1 = new Razorpay(options);

      rzp1.open();
    },

    error: function (error) {
      // Handle the error if any
      console.log(error);
    },
  });
};
