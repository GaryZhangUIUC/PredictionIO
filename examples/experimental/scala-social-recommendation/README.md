#Construction Plan
##Categorize Features
This facilitates the similarity calculation and future custom feature adding
##Algorithm
Examples are
http://blog.echen.me/2012/07/31/edge-prediction-in-a-social-graph-my-solution-to-facebooks-user-recommendation-contest-on-kaggle/
and
https://kaggle2.blob.core.windows.net/competitions/kddcup2012/2748/media/SJTU.pdf

For basic friend recommendation, the first blog post will be taken as example and more features will be defined as similarities using local features.
For source recommendation, first ALS will be used to get basic latent features. Then use followed relationship to propagate user features and category information to propagate item features.
