// Otorisasi berbasis ACTIVE ROLE (bukan sekadar daftar role yang dimiliki).
// `roles` = nama role lowercase yang diizinkan, mis. authorizeRoles('seller').
const authorizeRoles = (...roles) => {
  return (req, res, next) => {
    const active = req.user && req.user.activeRole;
    if (!active) {
      return res
        .status(403)
        .json({ success: false, message: 'Forbidden: Pilih peran aktif terlebih dahulu' });
    }
    if (!roles.map((r) => r.toLowerCase()).includes(String(active).toLowerCase())) {
      return res.status(403).json({ success: false, message: 'Forbidden: Insufficient permissions' });
    }
    next();
  };
};

module.exports = { authorizeRoles };
