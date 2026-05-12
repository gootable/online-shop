import request from './request'
import type { Result, User } from '../types'

export function login(username: string, password: string): Promise<Result<{ token: string; user: User }>> {
  return request.post('/auth/login', { username, password })
}

export function register(username: string, password: string, nickname?: string): Promise<Result<User>> {
  return request.post('/auth/register', { username, password, nickname })
}

export function getMe(): Promise<Result<User>> {
  return request.get('/auth/me')
}
