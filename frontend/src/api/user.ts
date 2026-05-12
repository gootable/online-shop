import request from './request'
import type { User, Result } from '../types'

export function getProfile(): Promise<Result<User>> {
  return request.get('/users/profile')
}

export function updateProfile(data: Partial<User>): Promise<Result<null>> {
  return request.put('/users/profile', data)
}

export function changePassword(oldPassword: string, newPassword: string): Promise<Result<null>> {
  return request.put('/users/password', { oldPassword, newPassword })
}
