export interface User {
  id: string;
  username: string;
  email: string;
  points: number;
  roles: string[];
  role?: 'buyer' | 'seller';
  isVerified?: boolean;
  cart?: CartItem[];
  wishlist?: string[];
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stock: number;
  category: string;
  imageUrl?: string;
}

export interface CartItem {
  productId: string;
  quantity: number;
}

export interface UserOrder {
  id: string;
  items: {
    productId: string;
    productName: string;
    productImage: string;
    quantity: number;
    price: number;
    sellerId?: string;
    status: string;
  }[];
  totalAmount: number;
  status: string;
  shippingAddress: string;
  createdAt: string;
  discountApplied: number;
}

export interface Review {
  id?: string;
  productId: string;
  userId: string;
  username: string;
  rating: number;
  comment: string;
  createdAt?: string;
}
