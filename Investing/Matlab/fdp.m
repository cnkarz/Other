%% 
%   FINANCIAL DATA PREDICTION
%     USING REGRESSION WITH REGULARIZATION 
%     AND GRADIENT DESCENT
%%

% This function performs the magic.

function [hx, yy, costArr] = fdp(data, period, pred, backdate)

    % index = 184;    % Old XU030
    % index = 197; % Old USDTRY
    %    index = 158;    % New XU030
     index = 171; % New USDTRY
    
    % omitted columns from X due to high correlation
    c = index;
    %omitted = [c (c-181) (c-179) (c-176) (c-173) (c-159) (c-157) (c-156),(c-155),(c-151) (c-149) (c-142):(c-133) (c-115):(c-109) (c-107),(c-104) (c-103) (c-78) (c-73) (c-72) (c-68) (c-66) (c-65), (c-58):(c-54) (c-51) (c-50) (c-48) (c-35) (c-28), (c-25):(c-1) (c+1):(c+8) (c+11)];
    %predictors = [26 124 150 151 152 194];
    %    predictors = [131 150 151 194 197]; % Old XU030 predictors
    % predictors = [105 124 125 168 171 173]; % New XU030 predictors
    % predictors = [1 21 150 152 158 194]; % Old USDTRY predictors
        
    %  predictors = [7 21 105 124 125 168]; % New XU030 predictors
     predictors = [1 21 124 126 132 168];% New USDTRY predictors
    
     % [Y_pre, X_pre] = slicer(data, index, omitted);
    [Y_pre, X_pre] = slicer2(data, index, predictors, backdate);
    
    i = 1;
    hx = zeros((length(Y_pre) - period),1);
    yy = zeros((length(Y_pre) - period - pred),1); 
    
    while length(Y_pre) ~= (i + period + pred-1)

        Y = Y_pre(i+pred : period+pred+i ,:);% -1
        X = X_pre(i:period+pred+i, :);

        Xtemp = normalizer(X);

        % +1 to manipulate row size to 50 and 3
        Xext = Xtemp(end-pred+1:end, :);% -1
        Xnorm = Xtemp(1:period+1, :);

        [hx(i), costArr, grad] = iterator2(Xnorm, Y); 

        
        yy(i) = Y(end);
        
        i = i + 1;
    
    end
 
   hx(i:end) = Xext * grad;
    
end


%{

Regression Analysis: XU030 versus VIX, WHEAT, YABTKSORAN, TRLON, CSTRY_2Y, AW01 

Coefficients

Term           Coef  SE Coef  T-Value  P-Value    VIF
Constant    -0.0000   0.0162    -0.00    1.000
VIX         -0.0653   0.0237    -2.75    0.007   2.14
WHEAT        0.1996   0.0289     6.90    0.000   3.18
YABTKSORAN   0.1145   0.0436     2.62    0.010   7.24
TRLON        0.7489   0.0593    12.63    0.000  13.36
CSTRY_2Y    -0.3010   0.0272   -11.07    0.000   2.81
AW01         0.1448   0.0546     2.65    0.009  11.34


Regression Analysis: USDTRY versus VIX, XAUUSD, YABTKSORAN, WTIUSD, AE01, CSTRY_2Y 

Coefficients

Term            Coef  SE Coef  T-Value  P-Value   VIF
Constant        2.27     1.11     2.05    0.042
VIX          -0.0474   0.0298    -1.59    0.115  2.17
XAUUSD       -0.0736   0.0461    -1.60    0.113  5.19
YABTKSORAN    0.2091   0.0470     4.45    0.000  5.39
WTIUSD        0.1128   0.0324     3.48    0.001  2.56
AE01        -0.00377  0.00184    -2.05    0.042  4.16
CSTRY_2Y      0.7247   0.0449    16.15    0.000  4.91
%}