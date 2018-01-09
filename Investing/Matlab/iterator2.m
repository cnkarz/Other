%% 
%   FINANCIAL DATA PREDICTION
%     USING REGRESSION WITH REGULARIZATION 
%     AND GRADIENT DESCENT
%%

% This function runs the iteration to minimize cost.

function [hx, costArr, grad] = iterator2(X, Y)

    theta_init = zeros(size(X,2),1);
    
    [J, grad] = costFun(theta_init, X, Y);
  
    oldCost = J;
    newCost = 0;
    theta = grad;
    costArr(1) = J;
  
    iter = 0;
  
    while (oldCost / newCost) > 1.001
     
      if iter > 0
        oldCost = newCost;  
      end
          
      [J, grad] = costFun(theta, X, Y);
      newCost = J;
      theta = grad;
      
      iter = iter + 1;
      costArr(iter + 1) = J;
      
      if iter > 5000
        break
      end
      
    end
    
    hx = X(size(X,1), :)* grad; 
    
end


