%% 
%   FINANCIAL DATA PREDICTION
%     USING REGRESSION WITH REGULARIZATION 
%     AND GRADIENT DESCENT
%%

% This function calculates the cost.

function [J, grad] = costFun(theta, X, Y)

  lambda = 0.5;
  len = length(Y);
  wid = length(theta);
  grad = zeros(wid, 1);
  
  alpha = 0.01;
  
  bias = theta(1);
  thetaRest = theta(2:end);
  
  hx = X * theta;
  
  J = (1 / (2 *len)) * ((hx - Y)' * (hx - Y) + lambda * (thetaRest' * thetaRest));
  
  Xo = X(:,1);
  Xrest = X(:, 2:end);
  
  biasTemp = (alpha / len) * sum(hx - Y);
  grad(1) = bias - biasTemp;
  
  first = thetaRest .* (1-lambda/len);
  second = bsxfun(@times, (alpha / len), sum(bsxfun(@times, (hx - Y), Xrest)));
  
  thetaRestTemp = first - second' ;
  
  grad(2:wid) = thetaRestTemp;
  
 end