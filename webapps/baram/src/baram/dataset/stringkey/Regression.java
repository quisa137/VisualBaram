package baram.dataset.stringkey;

import java.text.DecimalFormat;

public class Regression {
	
	private static DecimalFormat df = new DecimalFormat("0.###");
	
	String equation;
	double coefficients[];
	
	public String getEquation() {
		return equation;
	}
	
	public double getCoefficient(int order) {
		return coefficients[order];
	}
	
	public double getLinearPoint(double x) {
		return Math.round(1000.0*(coefficients[0]*x + coefficients[1]))/1000.0;
	}
	
	public static Regression linear(double data[][]) {
		Regression regression = new Regression();
		double sum[] = new double[5];
        for (int i=0; i < data.length; i++) {
        	sum[0] += data[i][0];
            sum[1] += data[i][1];
            sum[2] += data[i][0] * data[i][0];
            sum[3] += data[i][0] * data[i][1];
            sum[4] += data[i][1] * data[i][1];
        }
        double gradient = (data.length*sum[3]-sum[0]*sum[1])/(data.length*sum[2]-sum[0]*sum[0]);
        double intercept = (sum[1]/data.length) - (gradient * sum[0])/data.length;
        regression.coefficients = new double[2];
        regression.coefficients[0] = gradient;
        regression.coefficients[1] = intercept;
        
        double r = (data.length*sum[3]-sum[0]*sum[1])/Math.pow((data.length*sum[2]-sum[0]*sum[0])*(data.length*sum[4]-sum[1]*sum[1]), 0.5);
        
        if(intercept>=0) {
        	regression.equation = "y = " + df.format(gradient) + "x + " + df.format(intercept)+", r="+df.format(r);
        } else {
        	regression.equation = "y = " + df.format(gradient) + "x " + df.format(intercept)+", r="+df.format(r);
        }
        return regression;
    }
	
/*
    linearThroughOrigin: function(data) {
        var sum = [0, 0], n = 0, results = [];

        for (; n < data.length; n++) {
            if (data[n][1] != null) {
                sum[0] += data[n][0] * data[n][0]; //sumSqX
                sum[1] += data[n][0] * data[n][1]; //sumXY
            }
        }

        var gradient = sum[1] / sum[0];

        for (var i = 0, len = data.length; i < len; i++) {
            var coordinate = [data[i][0], data[i][0] * gradient];
            results.push(coordinate);
        }

        var string = 'y = ' + Math.round(gradient*100) / 100 + 'x';

        return {equation: [gradient], points: results, string: string};
    },

    exponential: function(data) {
        var sum = [0, 0, 0, 0, 0, 0], n = 0, results = [];

        for (len = data.length; n < len; n++) {
          if (data[n][1] != null) {
            sum[0] += data[n][0];
            sum[1] += data[n][1];
            sum[2] += data[n][0] * data[n][0] * data[n][1];
            sum[3] += data[n][1] * Math.log(data[n][1]);
            sum[4] += data[n][0] * data[n][1] * Math.log(data[n][1]);
            sum[5] += data[n][0] * data[n][1];
          }
        }

        var denominator = (sum[1] * sum[2] - sum[5] * sum[5]);
        var A = Math.pow(Math.E, (sum[2] * sum[3] - sum[5] * sum[4]) / denominator);
        var B = (sum[1] * sum[4] - sum[5] * sum[3]) / denominator;

        for (var i = 0, len = data.length; i < len; i++) {
            var coordinate = [data[i][0], A * Math.pow(Math.E, B * data[i][0])];
            results.push(coordinate);
        }

        var string = 'y = ' + Math.round(A*100) / 100 + 'e^(' + Math.round(B*100) / 100 + 'x)';

        return {equation: [A, B], points: results, string: string};
    },

    logarithmic: function(data) {
        var sum = [0, 0, 0, 0], n = 0, results = [];

        for (len = data.length; n < len; n++) {
          if (data[n][1] != null) {
            sum[0] += Math.log(data[n][0]);
            sum[1] += data[n][1] * Math.log(data[n][0]);
            sum[2] += data[n][1];
            sum[3] += Math.pow(Math.log(data[n][0]), 2);
          }
        }

        var B = (n * sum[1] - sum[2] * sum[0]) / (n * sum[3] - sum[0] * sum[0]);
        var A = (sum[2] - B * sum[0]) / n;

        for (var i = 0, len = data.length; i < len; i++) {
            var coordinate = [data[i][0], A + B * Math.log(data[i][0])];
            results.push(coordinate);
        }

        var string = 'y = ' + Math.round(A*100) / 100 + ' + ' + Math.round(B*100) / 100 + ' ln(x)';

        return {equation: [A, B], points: results, string: string};
    },

    power: function(data) {
        var sum = [0, 0, 0, 0], n = 0, results = [];

        for (len = data.length; n < len; n++) {
          if (data[n][1] != null) {
            sum[0] += Math.log(data[n][0]);
            sum[1] += Math.log(data[n][1]) * Math.log(data[n][0]);
            sum[2] += Math.log(data[n][1]);
            sum[3] += Math.pow(Math.log(data[n][0]), 2);
          }
        }

        var B = (n * sum[1] - sum[2] * sum[0]) / (n * sum[3] - sum[0] * sum[0]);
        var A = Math.pow(Math.E, (sum[2] - B * sum[0]) / n);

        for (var i = 0, len = data.length; i < len; i++) {
            var coordinate = [data[i][0], A * Math.pow(data[i][0] , B)];
            results.push(coordinate);
        }

         var string = 'y = ' + Math.round(A*100) / 100 + 'x^' + Math.round(B*100) / 100;

        return {equation: [A, B], points: results, string: string};
    },

    polynomial: function(data, order) {
        if(typeof order == 'undefined'){
            order = 2;
        }
         var lhs = [], rhs = [], results = [], a = 0, b = 0, i = 0, k = order + 1;

                for (; i < k; i++) {
                   for (var l = 0, len = data.length; l < len; l++) {
                      if (data[l][1] != null) {
                       a += Math.pow(data[l][0], i) * data[l][1];
                      }
                    }
                    lhs.push(a), a = 0;
                    var c = [];
                    for (var j = 0; j < k; j++) {
                       for (var l = 0, len = data.length; l < len; l++) {
                          if (data[l][1] != null) {
                           b += Math.pow(data[l][0], i + j);
                          }
                        }
                        c.push(b), b = 0;
                    }
                    rhs.push(c);
                }
        rhs.push(lhs);

       var equation = gaussianElimination(rhs, k);

            for (var i = 0, len = data.length; i < len; i++) {
                var answer = 0;
                for (var w = 0; w < equation.length; w++) {
                    answer += equation[w] * Math.pow(data[i][0], w);
                }
                results.push([data[i][0], answer]);
            }

            var string = 'y = ';

            for(var i = equation.length-1; i >= 0; i--){
              if(i > 1) string += Math.round(equation[i] * Math.pow(10, i)) / Math.pow(10, i)  + 'x^' + i + ' + ';
              else if (i == 1) string += Math.round(equation[i]*100) / 100 + 'x' + ' + ';
              else string += Math.round(equation[i]*100) / 100;
            }

        return {equation: equation, points: results, string: string};
    },

    lastvalue: function(data) {
      var results = [];
      var lastvalue = null;
      for (var i = 0; i < data.length; i++) {
        if (data[i][1]) {
          lastvalue = data[i][1];
          results.push([data[i][0], data[i][1]]);
        }
        else {
          results.push([data[i][0], lastvalue]);
        }
      }

      return {equation: [lastvalue], points: results, string: "" + lastvalue};
    }
	 });
	
	public static gaussianElimination() {
		
	}
	
	var gaussianElimination = function(a, o) {
        var i = 0, j = 0, k = 0, maxrow = 0, tmp = 0, n = a.length - 1, x = new Array(o);
        for (i = 0; i < n; i++) {
           maxrow = i;
           for (j = i + 1; j < n; j++) {
              if (Math.abs(a[i][j]) > Math.abs(a[i][maxrow]))
                 maxrow = j;
           }
           for (k = i; k < n + 1; k++) {
              tmp = a[k][i];
              a[k][i] = a[k][maxrow];
              a[k][maxrow] = tmp;
           }
           for (j = i + 1; j < n; j++) {
              for (k = n; k >= i; k--) {
                 a[k][j] -= a[k][i] * a[i][j] / a[i][i];
              }
           }
        }
        for (j = n - 1; j >= 0; j--) {
           tmp = 0;
           for (k = j + 1; k < n; k++)
              tmp += a[k][j] * x[k];
           x[j] = (a[n][j] - tmp) / a[j][j];
        }
        return (x);
 };

*/
}
